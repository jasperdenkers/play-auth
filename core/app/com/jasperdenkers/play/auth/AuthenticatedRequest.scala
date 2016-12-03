package com.jasperdenkers.play.auth

import play.api.mvc.{Request, WrappedRequest}

class AuthenticatedRequest[A, B](val identity: A, val request: Request[B]) extends WrappedRequest[B](request)

object AuthenticatedRequest {

  def apply[A, B](identity: A, request: Request[B]) = new AuthenticatedRequest(identity, request)

  def unapply[A, B](authenticatedRequest: AuthenticatedRequest[A, B]) =
    Some(authenticatedRequest.identity, authenticatedRequest.request)

}
