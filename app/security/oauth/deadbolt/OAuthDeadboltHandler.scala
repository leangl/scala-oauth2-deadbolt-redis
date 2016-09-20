package security.oauth.deadbolt

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.google.inject.Inject
import play.api.mvc.{Request, Result, Results}
import security.oauth.{AccountInfo, OAuthDataHandler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaoauth2.provider.{AuthInfo, OAuth2ProtectedResourceProvider}

/**
  * Deadbolt handler that delegates to the oauth2 data handler
  *
  * @param dataHandler
  */
class OAuthDeadboltHandler @Inject()(dataHandler: OAuthDataHandler) extends DeadboltHandler with OAuth2ProtectedResourceProvider {

  val dynamicHandler: Option[DynamicResourceHandler] = Option.empty

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future(None)

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future.successful(dynamicHandler)

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] =
    request.subject match {
      case Some(subject) => Future.successful(request.subject)
      case _ => protectedResource.handleRequest(request, dataHandler).map {
        case Left(e) => None
        case Right(authInfo: AuthInfo[AccountInfo]) => Some(new OAuthSubject(authInfo))
      }
    }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = Future(Results.Unauthorized)
}