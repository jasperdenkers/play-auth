package com.jasperdenkers.play.auth

import play.api.mvc.{ActionBuilder, Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Authentication[A] {

  def authenticator: Authenticator[A]

  type AuthenticatedRequestWithIdentity[B] = AuthenticatedRequest[A, B]

  def onUnauthenticated: Future[Result] = authenticator.notAuthenticatedResult

  object Authenticated extends ActionBuilder[AuthenticatedRequestWithIdentity] {
    def invokeBlock[B](request: Request[B], block: (AuthenticatedRequestWithIdentity[B]) => Future[Result]) =
      authenticator.authenticatedIdentity(request).flatMap {
        case Some(identity) => block(AuthenticatedRequest(identity, request))
        case None => onUnauthenticated
      }
  }

  object MaybeAuthenticated extends ActionBuilder[Request] {
    def invokeBlock[B](request: Request[B], block: (Request[B]) => Future[Result]) =
      authenticator.authenticatedIdentity(request).flatMap {
        case Some(identity) => block(AuthenticatedRequest(identity, request))
        case None           => block(request)
      }
  }

}
