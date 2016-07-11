package security.oauth.deadbolt

import be.objectify.deadbolt.scala.models.{Permission, Role}

/**
  * Deadbolt permission implementing both Java and Scala interfaces
  *
  * Created by lglossman on 7/7/16.
  */
class OAuthScope(scope: String) extends Role with Permission with be.objectify.deadbolt.java.models.Role
  with be.objectify.deadbolt.java.models.Permission {

  override def name: String = scope

  override def value: String = scope

  override def getName: String = scope

  override def getValue: String = scope
}

object OAuthScope {

  def apply(scopeString: String): List[OAuthScope] = scopeString.split(",").toList.map(s => new OAuthScope(s.trim))

}