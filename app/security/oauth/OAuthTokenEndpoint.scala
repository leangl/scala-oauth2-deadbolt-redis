package security.oauth

import javax.inject.Singleton

import scalaoauth2.provider._

/**
  * Created by lglossman on 10/5/16.
  */
@Singleton
class OAuthTokenEndpoint extends TokenEndpoint {
  override val handlers = Map(
    OAuthGrantType.AUTHORIZATION_CODE -> new AuthorizationCode(),
    OAuthGrantType.REFRESH_TOKEN -> new RefreshToken(),
    //OAuthGrantType.CLIENT_CREDENTIALS -> new ClientCredentials(),
    //OAuthGrantType.IMPLICIT -> new Implicit(),
    OAuthGrantType.PASSWORD -> new Password()
  )
}