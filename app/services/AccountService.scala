package services

import java.time.{Clock, Instant}
import javax.inject._

import org.mindrot.jbcrypt.BCrypt
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

/**
  * Mock service that simply returns a hardcoded user. Replace this by a real service implementation.
  */
@Singleton
class AccountService() {

  val dummyUser = User("test", BCrypt.hashpw("12345", BCrypt.gensalt()))

  def getUser(username: String): Option[User] = {
    username match {
      case dummyUser.username => Some(dummyUser)
      case _ => None
    }
  }

}

case class User(username: String, password: String) {

  def isPasswordValid(candidate: String): Boolean = BCrypt.checkpw(candidate, password)

}