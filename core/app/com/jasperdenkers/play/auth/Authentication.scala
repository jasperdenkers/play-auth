package com.jasperdenkers.play.auth

import play.api.mvc.{ActionBuilder, Cookie, Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Authentication[A] extends AuthRequests[A] {

  def authenticator: Authenticator[A]

  def onUnauthenticated: Future[Result] = authenticator.notAuthenticatedResult

  object Authenticated extends ActionBuilder[AuthenticatedRequestWithIdentity] {
    def invokeBlock[B](request: Request[B], block: (AuthenticatedRequestWithIdentity[B]) => Future[Result]) =
      authenticated(request, block)
  }

  object MaybeAuthenticated extends ActionBuilder[Request] {
    def invokeBlock[B](request: Request[B], block: (Request[B]) => Future[Result]) =
      maybeAuthenticated(request, block)
  }

  protected def authenticated[B](request: Request[B], block: (AuthenticatedRequestWithIdentity[B]) => Future[Result]) =
    authenticator.authenticatedIdentity(request).flatMap {
      case Some(identity) => block(AuthenticatedRequest(identity, request))
      case None => onUnauthenticated
    }

  protected def maybeAuthenticated[B](request: Request[B], block: (Request[B]) => Future[Result]) =
    authenticator.authenticatedIdentity(request).flatMap {
      case Some(identity) => block(AuthenticatedRequest(identity, request))
      case None           => block(request)
    }

}

trait SessionAuthentication[A, B <: AnyRef] extends Authentication[A] {

  override def authenticator: SessionAuthenticator[A, B]

  override def authenticated[C](request: Request[C], block: (AuthenticatedRequestWithIdentity[C]) => Future[Result]) =
    authenticator.authenticatedIdentityWithUpdatedSession(request).flatMap {
      case Some((identity, updatedSession)) =>
        for {
          _      <- updatedSessionHook(updatedSession)
          result <- block(AuthenticatedRequest(identity, request))
        } yield result
      case None => onUnauthenticated
    }

  override def maybeAuthenticated[C](request: Request[C], block: (Request[C]) => Future[Result]) =
    authenticator.authenticatedIdentityWithUpdatedSession(request).flatMap {
      case Some((identity, updatedSession)) =>
        for {
          _      <- updatedSessionHook(updatedSession)
          result <- block(AuthenticatedRequest(identity, request))
        } yield result
      case None => block(request)
    }

  def updatedSessionHook(session: B): Future[Unit] = Future.successful(())

}

trait SessionCookieAuthentication[A, B <: AnyRef] extends SessionAuthentication[A, B] {

  override def authenticator: SessionCookieAuthenticator[A, B]

  override def authenticated[C](request: Request[C], block: (AuthenticatedRequestWithIdentity[C]) => Future[Result]) =
    authenticator.authenticatedIdentityWithUpdatedSessionCookie(request).flatMap {
      case Some((identity, updatedSession, updatedSessionCookie)) =>
        for {
          _      <- updatedSessionHook(updatedSession)
          _      <- updatedSessionCookieHook(updatedSession, updatedSessionCookie)
          result <- block(AuthenticatedRequest(identity, request)).map(_.withCookies(updatedSessionCookie))
        } yield result
      case None => onUnauthenticated
    }

  override def maybeAuthenticated[C](request: Request[C], block: (Request[C]) => Future[Result]) =
    authenticator.authenticatedIdentityWithUpdatedSessionCookie(request).flatMap {
      case Some((identity, updatedSession, updatedSessionCookie)) =>
        for {
          _      <- updatedSessionHook(updatedSession)
          _      <- updatedSessionCookieHook(updatedSession, updatedSessionCookie)
          result <- block(AuthenticatedRequest(identity, request)).map(_.withCookies(updatedSessionCookie))
        } yield result
      case None => block(request)
    }

  def updatedSessionCookieHook(session: B, cookie: Cookie): Future[Unit] = Future.successful(())

}