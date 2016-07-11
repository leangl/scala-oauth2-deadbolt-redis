package security.oauth.deadbolt

import java.util.Optional
import java.util.concurrent.{CompletableFuture, CompletionStage}
import javax.inject.{Inject, Singleton}

import be.objectify.deadbolt.java.cache.HandlerCache
import be.objectify.deadbolt.java.models.Subject
import be.objectify.deadbolt.java.{DeadboltHandler, DynamicResourceHandler}
import be.objectify.deadbolt.scala.AuthenticatedRequest
import play.mvc.Http.Context
import play.mvc.Result

import scala.compat.java8.FutureConverters
import scala.compat.java8.OptionConverters._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Java implementation of Deadbolt handlers that act as a wrapper for the real scala implementations.
  *
  * Created by lglossman on 9/7/16.
  */
@Singleton
class JavaCompat @Inject()(scalaCache: be.objectify.deadbolt.scala.cache.HandlerCache) extends HandlerCache with DeadboltHandler {

  override def getDynamicResourceHandler(context: Context): CompletionStage[Optional[DynamicResourceHandler]] = {
    CompletableFuture.completedFuture(Optional.empty.asInstanceOf)
  }

  override def beforeAuthCheck(context: Context): CompletionStage[Optional[Result]] = {
    FutureConverters.toJava(
      scalaCache
        .withCaching
        .beforeAuthCheck(context.request()._underlyingRequest())
        .map(o => o.map(r => r.asJava).asJava)
    )
  }

  override def getSubject(context: Context): CompletionStage[Optional[_ <: Subject]] = {
    FutureConverters.toJava(
      scalaCache
        .withCaching
        .getSubject(AuthenticatedRequest(context.request()._underlyingRequest(), Option.empty))
        .map(o => o.map(s => s.asInstanceOf[OAuthSubject]).asJava)
    )
  }

  override def onAuthFailure(context: Context, content: Optional[String]): CompletionStage[Result] = {
    FutureConverters.toJava(
      scalaCache
        .withCaching
        .onAuthFailure(AuthenticatedRequest(context.request()._underlyingRequest(), Option.empty))
        .map(r => r.asJava)
    )
  }

  override def get(): DeadboltHandler = this

  override def apply(t: String): DeadboltHandler = this

}
