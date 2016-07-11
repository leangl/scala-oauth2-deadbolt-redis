package security.oauth.deadbolt

import javax.inject.{Inject, Singleton}

import be.objectify.deadbolt.java.DeadboltHandler
import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}

@Singleton
class DeadboltHandlerCache @Inject()(defaultHandler: OAuthDeadboltHandler) extends HandlerCache {

  override def apply(): be.objectify.deadbolt.scala.DeadboltHandler = defaultHandler

  override def apply(handlerKey: HandlerKey): be.objectify.deadbolt.scala.DeadboltHandler = defaultHandler

}