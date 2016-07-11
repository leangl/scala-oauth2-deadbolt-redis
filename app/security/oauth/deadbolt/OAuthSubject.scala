package security.oauth.deadbolt

import java.util

import be.objectify.deadbolt.scala.models.Subject
import security.oauth.AccountInfo

import scala.collection.JavaConverters._
import scalaoauth2.provider.AuthInfo

/**
  * Deadbolt subject implementing both Java and Scala interfaces
  *
  * Created by lglossman on 7/7/16.
  */
class OAuthSubject(authInfo: AuthInfo[AccountInfo]) extends Subject with be.objectify.deadbolt.java.models.Subject {

  val scopes = OAuthScope(authInfo.scope.get)

  override def identifier: String = authInfo.user.username

  override def permissions: List[be.objectify.deadbolt.scala.models.Permission] = scopes

  override def roles: List[be.objectify.deadbolt.scala.models.Role] = scopes

  override def getRoles: util.List[_ <: be.objectify.deadbolt.java.models.Role] = scopes.asJava

  override def getPermissions: util.List[_ <: be.objectify.deadbolt.java.models.Permission] = scopes.asJava

  override def getIdentifier: String = authInfo.user.username
}
