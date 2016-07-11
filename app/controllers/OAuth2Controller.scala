package controllers

import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import security.oauth.{OAuthDataHandler, OAuthTokenEndpoint}

import scala.concurrent.ExecutionContext.Implicits.global
import scalaoauth2.provider.OAuth2Provider

/**
  * Controller that acts as the entry point for the OAuth2 authentication.
  *
  * Created by lglossman on 10/5/16.
  */
class OAuth2Controller @Inject()(dataHandler: OAuthDataHandler) extends Controller with OAuth2Provider {

  override val tokenEndpoint = new OAuthTokenEndpoint

  def token = Action.async { implicit request =>
    issueAccessToken(dataHandler)
  }

}
