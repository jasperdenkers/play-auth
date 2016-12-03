package com.jasperdenkers.play.auth

import play.api.mvc.Request

case class AuthorizedRequest[A, B](override val identity: A, tokens: Set[Token], override val request: Request[B])
    extends AuthenticatedRequest[A, B](identity, request) {

  def isAuthorized(capabilities: Set[Capability]): Boolean = capabilities.forall(_.authorize(tokens))

  def isAuthorized(capabilities: Capability*): Boolean = isAuthorized(capabilities.toSet)

}

object AuthorizedRequest {

  def fromAuthenticatedRequest[A, B](authenticatedRequest: AuthenticatedRequest[A, B], tokens: Set[Token]) =
    AuthorizedRequest(authenticatedRequest.identity, tokens, authenticatedRequest.request)

}
