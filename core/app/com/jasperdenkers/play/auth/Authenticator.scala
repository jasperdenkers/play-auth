package com.jasperdenkers.play.auth

import play.api.Configuration
import play.api.libs.crypto.CookieSignerProvider
import play.api.mvc.{Cookie, CookieBaker, RequestHeader, Result}

import scala.concurrent.Future

trait Authenticator[A] {

  type AuthenticatedRequestWithIdentity[B] = AuthenticatedRequest[A, B]

  def authenticatedIdentity(request: RequestHeader): Future[Option[A]]

  def notAuthenticatedResult: Future[Result]

}

trait SessionCookieAuthenticator[A, B <: AnyRef] extends Authenticator[A] {

  def configuration: Configuration
  def cookieSignerProvider: CookieSignerProvider

  val cookieName             = configuration.getString("auth.sessionCookieName").getOrElse("session")
  val cookieMaxAge           = configuration.getInt("auth.sessionCookieMaxAge").getOrElse(3600) // One hour
  val cookieMaxAgeRemembered = configuration.getInt("auth.sessionCookieMaxAgeRemembered").getOrElse(7 * 24 * 3600) // One week

  def emptySession: B

  def serializeSession(session: B): Map[String, String]

  def deserializeSession(map: Map[String, String]): Option[B]

  trait SessionCookieBaker extends CookieBaker[B] {
    val COOKIE_NAME                           = cookieName
    val emptyCookie                           = emptySession
    override val isSigned                     = true
    val cookieSigner                          = cookieSignerProvider.get
    def serialize(session: B)                 = serializeSession(session)
    def deserialize(map: Map[String, String]) = deserializeSession(map).getOrElse(emptySession)
  }

  object SessionCookieBaker extends SessionCookieBaker

  object TransientSessionCookieBaker extends SessionCookieBaker {
    override val maxAge = None
  }

  object PersistentSessionCookieBaker extends SessionCookieBaker {
    override val maxAge = Some(cookieMaxAgeRemembered)
  }

  def login(loginData: LoginData): Future[Option[Cookie]]

  def logout(request: RequestHeader, redirectAfterLogout: Result): Future[Result]

}