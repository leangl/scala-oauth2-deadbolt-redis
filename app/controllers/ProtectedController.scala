package controllers

import javax.inject.Inject

import be.objectify.deadbolt.scala._
import play.api.mvc.Controller
import security.oauth.OAuthDataHandler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider.OAuth2Provider

/**
  * Example of a protected controller.
  *
  * Created by lglossman on 11/7/16.
  */
class ProtectedController @Inject()(actionBuilder: ActionBuilders, dataHandler: OAuthDataHandler) extends Controller with OAuth2Provider {

  def tokenPresent = actionBuilder.SubjectPresentAction().defaultHandler() { implicit request =>
    Future(Ok)
  }

  def inclusiveScopes = actionBuilder.RestrictAction(anyOf(allOf("posts"), allOf("someOtherScope"))).defaultHandler() { implicit request =>
    Future(Ok)
  }

  def exclusiveScopes = actionBuilder.RestrictAction("posts", "profile").defaultHandler() { implicit request =>
    Future(Ok)
  }

  def singleScope = actionBuilder.RestrictAction("profile").defaultHandler() { implicit request =>
    Future(Ok)
  }

  def anotherSingleScope = actionBuilder.RestrictAction("someOtherScope").defaultHandler() { implicit request =>
    Future(Ok)
  }

}
