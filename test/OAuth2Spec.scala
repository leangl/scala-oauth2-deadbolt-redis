import matchers.JsonMatchers
import org.scalatestplus.play._
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._

/**
  * Some quick tests for the OAuth2 authentication.
  *
  */
class OAuth2Spec extends PlaySpec with OneAppPerTest with JsonMatchers {

  "OAuth2Controller" should {

    var token: JsValue = null

    "issue a new access token" in {
      val result = route(app, FakeRequest(POST, "/oauth2/token") withFormUrlEncodedBody(
        "grant_type" -> "password",
        "scope" -> "profile,posts",
        "username" -> "test",
        "password" -> "12345",
        "client_id" -> "1"
        )).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")

      token = Json.parse(contentAsString(result))

      token must containKey("access_token")
      token must containKey("expires_in")
      token must containValue(__ \ "scope", JsString("profile,posts"))
      token must containKey("refresh_token")
      token must containValue(__ \ "token_type", JsString("Bearer"))
    }

    "refresh access token" in {

      val result = route(app, FakeRequest(POST, "/oauth2/token") withFormUrlEncodedBody(
        "grant_type" -> "refresh_token",
        "refresh_token" -> (token \ "refresh_token").asOpt[String].get,
        "client_id" -> "1"
        )).get

      status(result) mustBe OK
      contentType(result) mustBe Some("application/json")

      token = Json.parse(contentAsString(result))

      token must containKey("access_token")
      token must containKey("expires_in")
      token must containValue(__ \ "scope", JsString("profile,posts"))
      token must containKey("refresh_token")
      token must containValue(__ \ "token_type", JsString("Bearer"))
    }

    "prevent access if token is not present or invalid" in {

      val result = route(app, FakeRequest(GET, "/oauth2/test")).get

      status(result) mustBe UNAUTHORIZED

    }

    "allow access if token is present and valid" in {

      val result = route(app, FakeRequest(GET, "/oauth2/test") withHeaders (
        "Authorization" -> s"Bearer ${(token \ "access_token").asOpt[String].get}"
        )).get

      status(result) mustBe OK

    }

    "prevent access if token scope is not sufficient" in {

      val result = route(app, FakeRequest(GET, "/oauth2/test/scope/anotherSingle") withHeaders (
        "Authorization" -> s"Bearer ${(token \ "access_token").asOpt[String].get}"
        )).get

      status(result) mustBe UNAUTHORIZED

    }

    "allow access if token scope is sufficient" in {

      val result = route(app, FakeRequest(GET, "/oauth2/test/scope/single") withHeaders (
        "Authorization" -> s"Bearer ${(token \ "access_token").asOpt[String].get}"
        )).get

      status(result) mustBe OK

    }

    "allow access if token has all of the required scopes" in {

      val result = route(app, FakeRequest(GET, "/oauth2/test/scope/exclusive") withHeaders (
        "Authorization" -> s"Bearer ${(token \ "access_token").asOpt[String].get}"
        )).get

      status(result) mustBe OK

    }

    "allow access if token has at least one of the required scopes" in {

      val result = route(app, FakeRequest(GET, "/oauth2/test/scope/inclusive") withHeaders (
        "Authorization" -> s"Bearer ${(token \ "access_token").asOpt[String].get}"
        )).get

      status(result) mustBe OK

    }




    "(JAVA) prevent access if token is not present or invalid" in {

      val result = route(app, FakeRequest(GET, "/oauth2/test/java")).get

      status(result) mustBe UNAUTHORIZED

    }

    "(JAVA) allow access if token is present and valid" in {

      val result = route(app, FakeRequest(GET, "/oauth2/test/java") withHeaders (
        "Authorization" -> s"Bearer ${(token \ "access_token").asOpt[String].get}"
        )).get

      status(result) mustBe OK

    }

    "(JAVA) allow access if token scope is sufficient" in {

      val result = route(app, FakeRequest(GET, "/oauth2/test/java/scope/single") withHeaders (
        "Authorization" -> s"Bearer ${(token \ "access_token").asOpt[String].get}"
        )).get

      status(result) mustBe OK

    }

  }


}
