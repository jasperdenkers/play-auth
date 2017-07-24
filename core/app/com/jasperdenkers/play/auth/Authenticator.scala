package com.jasperdenkers.play.auth

import play.api.Configuration
import play.api.http.{JWTConfiguration, SecretConfiguration}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Authenticator[A] {

  def authenticatedIdentity(request: RequestHeader): Future[Option[A]]

  def notAuthenticatedResult[B](request: Request[B]): Future[Result]

}

trait SessionAuthenticator[A, B <: AnyRef] extends Authenticator[A] {

  def sessionFromRequest(request: RequestHeader): B

  def authenticatedIdentity(session: B): Future[Option[A]]

  def authenticatedIdentity(request: RequestHeader) =
    authenticatedIdentity(sessionFromRequest(request))

  def authenticatedIdentityWithUpdatedSession(request: RequestHeader): Future[Option[(A, B)]] = {
    val session = sessionFromRequest(request)

    authenticatedIdentity(session).flatMap {
      case Some(identity) => updateSession(session).map { updatedSession =>
        Some((identity, updatedSession))
      }
      case None => Future.successful(None)
    }
  }

  def updateSession(session: B): Future[B] = Future.successful(session)

}

trait SessionCookieAuthenticator[A, B <: AnyRef] extends SessionAuthenticator[A, B] { sca =>

  def configuration: Configuration
  def secretConfiguration: SecretConfiguration

  val cookieName             = configuration.getOptional[String]("auth.sessionCookieName").getOrElse("session")
  val cookieMaxAge           = configuration.getOptional[Int]("auth.sessionCookieMaxAge").getOrElse(3600) // One hour
  val cookieMaxAgeRemembered = configuration.getOptional[Int]("auth.sessionCookieMaxAgeRemembered").getOrElse(7 * 24 * 3600) // One week

  def emptySession: B

  def serializeSession(session: B): Map[String, String]

  def deserializeSession(map: Map[String, String]): Option[B]

  trait SessionCookieBaker extends CookieBaker[B] with JWTCookieDataCodec {
    val secretConfiguration                   = sca.secretConfiguration
    val COOKIE_NAME                           = cookieName
    override val isSigned                     = true
    val emptyCookie                           = emptySession
    def serialize(session: B)                 = serializeSession(session)
    def deserialize(map: Map[String, String]) = deserializeSession(map).getOrElse(emptySession)
    val path: String = "/"
    val jwtConfiguration = JWTConfiguration()
  }

  object SessionCookieBaker extends SessionCookieBaker

  object TransientSessionCookieBaker extends SessionCookieBaker {
    override val maxAge = None
  }

  object PersistentSessionCookieBaker extends SessionCookieBaker {
    override val maxAge = Some(cookieMaxAgeRemembered)
  }

  def sessionFromCookie(cookie: Option[Cookie]) =
    SessionCookieBaker.decodeFromCookie(cookie)

  def sessionFromRequest(request: RequestHeader) =
    sessionFromCookie(request.cookies.get(cookieName))

  def authenticatedIdentityWithUpdatedSessionCookie(request: RequestHeader): Future[Option[(A, B, Cookie)]] =
    authenticatedIdentityWithUpdatedSession(request).flatMap {
      case Some((identity, session)) =>
        val originalCookie = request.cookies.get(cookieName)

        updateSessionWithCookie(session, originalCookie).map { updatedSession =>
          val cookieBaker =
            originalCookie match {
              case Some(cookie) if cookie.maxAge.nonEmpty => PersistentSessionCookieBaker
              case _ => TransientSessionCookieBaker
            }

          val updatedSessionCookie = cookieBaker.encodeAsCookie(updatedSession)

          Some((identity, updatedSession, updatedSessionCookie))
        }
      case None => Future.successful(None)
    }

  def updateSessionWithCookie(session: B, cookie: Option[Cookie]): Future[B] = Future.successful(session)

  def login(loginData: LoginData): Future[Option[Cookie]]

  def logout(request: RequestHeader, redirectAfterLogout: Result): Future[Result]

}