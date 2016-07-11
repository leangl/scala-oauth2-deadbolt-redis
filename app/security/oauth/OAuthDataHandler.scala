package security.oauth

import javax.inject.Inject

import org.joda.time.DateTime
import org.sedis.Pool
import play.api.{Configuration, Logger}
import play.api.libs.Crypto
import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, Json, _}
import services.AccountService

import scala.concurrent.Future
import scalaoauth2.provider.{AccessToken, AuthInfo, AuthorizationRequest, DataHandler}

/**
  * OAuth2 Handler that stores tokens in Redis.
  *
  * https://github.com/nulab/scala-oauth2-provider
  *
  * Created by lglossman on 10/5/16.
  */
class OAuthDataHandler @Inject()(accountService: AccountService, sedisPool: Pool, config: Configuration) extends DataHandler[AccountInfo] {

  val accessTokenExpire = Some(config.getMilliseconds("oauth2.tokenExpire").getOrElse(60 * 60L * 1000) / 1000)

  def validateClient(request: AuthorizationRequest): Future[Boolean] = {
    // TODO validate client
    Future.successful(true)
  }

  def findUser(request: AuthorizationRequest): Future[Option[AccountInfo]] = {
    Future.successful(
      accountService.getUser(request.param("username").get) match {
        case Some(user) if user.isPasswordValid(request.param("password").get) => Some(AccountInfo(user.username))
        case Some(user) => None
        case None => None
      }
    )
  }

  def createAccessToken(authInfo: AuthInfo[AccountInfo]): Future[AccessToken] = {
    val refreshToken = Some(Crypto.generateToken)
    val accessToken = Crypto.generateToken
    val now = DateTime.now().toDate

    val tokenObject = AccessToken(accessToken, refreshToken, authInfo.scope, accessTokenExpire, now)
    saveToken(authInfo, tokenObject)

    Future.successful(tokenObject)
  }

  private def saveToken(authInfo: AuthInfo[AccountInfo], tokenObject: AccessToken) = sedisPool.withClient { w =>
    val username = authInfo.user.username
    val clientId = authInfo.clientId.get

    for (existing <- getAccessToken(username, clientId)) {
      w.del(s"oauth:refresh_token:${existing.refreshToken.get}")
      w.del(s"oauth:access_token:${existing.token}")
    }

    w.set(key(username, clientId), Json.stringify(Json.toJson(tokenObject)))
    w.expire(key(username, clientId), tokenObject.expiresIn.get.toInt)

    w.set(s"oauth:refresh_token:${tokenObject.refreshToken.get}", Json.stringify(Json.toJson(authInfo)))
    w.expire(s"oauth:refresh_token:${tokenObject.refreshToken.get}", tokenObject.expiresIn.get.toInt)

    w.set(s"oauth:access_token:${tokenObject.token}", Json.stringify(Json.toJson(authInfo)))
    w.expire(s"oauth:access_token:${tokenObject.token}", tokenObject.expiresIn.get.toInt)
  }

  def key(username: String, clientId: String) = s"oauth:$username:$clientId"

  def getStoredAccessToken(authInfo: AuthInfo[AccountInfo]): Future[Option[AccessToken]] = {
    Future.successful(getAccessToken(authInfo.user.username, authInfo.clientId.get) match {
      case Some(token) if token.scope.equals(authInfo.scope) => Some(token)
      case _ => None // no previous token or scope changed
    })
  }

  private def getAccessToken(username: String, clientId: String): Option[AccessToken] = sedisPool.withClient { w =>
    w.get(key(username, clientId)).flatMap(Json.parse(_).validate[AccessToken].asOpt)
  }

  def refreshAccessToken(authInfo: AuthInfo[AccountInfo], refreshToken: String): Future[AccessToken] = {
    val accessToken = Crypto.generateToken
    val now = DateTime.now().toDate
    val tokenObject = AccessToken(accessToken, Some(refreshToken), authInfo.scope, accessTokenExpire, now)

    saveToken(authInfo, tokenObject)

    Future.successful(tokenObject)
  }

  def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[AccountInfo]]] = {
    Future.successful(sedisPool.withClient { w =>
      w.get(s"oauth:access_token:${accessToken.token}")
        .flatMap(Json.parse(_).validate[AuthInfo[AccountInfo]].asOpt)
    })
  }

  def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[AccountInfo]]] = {
    Future.successful(sedisPool.withClient { w =>
      w.get(s"oauth:refresh_token:$refreshToken")
        .flatMap(Json.parse(_).validate[AuthInfo[AccountInfo]].asOpt)
    })
  }

  def findAuthInfoByCode(code: String): Future[Option[AuthInfo[AccountInfo]]] = {
    Future.failed(new NotImplementedError)
  }

  def deleteAuthCode(code: String): Future[Unit] = {
    Future.failed(new NotImplementedError)
  }

  def findAccessToken(token: String): Future[Option[AccessToken]] = {
    Future.successful(sedisPool.withClient { w =>
      w.get(s"oauth:access_token:$token")
        .flatMap(Json.parse(_).validate[AuthInfo[AccountInfo]].asOpt)
        .flatMap(authInfo => w.get(key(authInfo.user.username, authInfo.clientId.get)))
        .flatMap(Json.parse(_).validate[AccessToken].asOpt)
    })
  }

  implicit val tokenFormat = Json.format[AccessToken]
  implicit val accountInfoFormat = Json.format[AccountInfo]
  implicit val authInfoFormat: Format[AuthInfo[AccountInfo]] =
    ((__ \ "user").format[AccountInfo] ~
      (__ \ "clientId").formatNullable[String] ~
      (__ \ "scope").formatNullable[String] ~
      (__ \ "redirectUri").formatNullable[String]) (AuthInfo.apply, unlift(AuthInfo.unapply))
}

