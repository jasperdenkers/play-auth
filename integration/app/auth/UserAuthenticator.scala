package auth

import java.time.Instant
import javax.inject.Inject

import com.jasperdenkers.play.auth.{LoginData, SessionCookieAuthenticator}
import models.{User, UserRepository}
import play.api.Configuration
import play.api.libs.crypto.CookieSignerProvider
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

case class Session(username: String, expires: Instant, requestCount: Int)

object Session {
  def empty = Session("", Instant.now, 0)
}

class UserAuthenticator @Inject()(val configuration: Configuration, val cookieSignerProvider: CookieSignerProvider) extends SessionCookieAuthenticator[User, Session] with Results {

  def emptySession = Session.empty

  def serializeSession(session: Session) =
    Map(
      "username" -> session.username,
      "expires" -> session.expires.getEpochSecond.toString,
      "requestCount" -> session.requestCount.toString
    )

  def deserializeSession(map: Map[String, String]) = for {
    username <- map.get("username")
    expires <- map.get("expires").flatMap {
      epochSecondString => Try(epochSecondString.toLong).toOption.map(Instant.ofEpochSecond)
    }
    requestCount <- map.get("requestCount").flatMap {
      requestCountString => Try(requestCountString.toInt).toOption
    }
  } yield Session(username, expires, requestCount)

  def authenticatedIdentity(session: Session) =
    UserRepository.find(session.username)

  override def updateSession(session: Session): Future[Session] = Future.successful(session.copy(requestCount = session.requestCount + 1))

  def notAuthenticatedResult =
    Future.successful {
      Redirect(controllers.routes.LoginLogout.login())
    }

  def login(loginData: LoginData) =
    UserRepository.find(loginData.identifier).map {
      case Some(user) if user.checkPassword(loginData.plaintextPassword) =>
        val expiryTime = if (loginData.remember) cookieMaxAgeRemembered else cookieMaxAge
        val expires    = Instant.now.plusSeconds(expiryTime)

        val session = Session(user.username, expires, 0)

        val cookieBaker =
          if (loginData.remember)
            PersistentSessionCookieBaker
          else
            TransientSessionCookieBaker

        val cookie = cookieBaker.encodeAsCookie(session)

        Some(cookie)
      case _ => None
    }

  def logout(requestHeader: RequestHeader, redirectAfterLogout: Result) = {
    val discardingCookie = DiscardingCookie(cookieName)

    Future.successful {
      redirectAfterLogout.discardingCookies(discardingCookie)
    }
  }

}