package com.jasperdenkers.play.auth

import play.api.mvc.Result

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Authorizator[A] extends AuthRequests[A] {

  def getTokens[B](authenticatedRequest: AuthenticatedRequestWithIdentity[B]): Future[Set[Token]]

  def isAuthorized[B](
      authenticatedRequest: AuthenticatedRequestWithIdentity[B],
      capability: Capability
  ): Future[Boolean] =
    isAuthorized(authenticatedRequest, Set(capability))

  def isAuthorized[B](
      authenticatedRequest: AuthenticatedRequestWithIdentity[B],
      capabilities: Set[Capability]
  ): Future[Boolean] =
    getTokens(authenticatedRequest).map(isAuthorized(capabilities, _))

  def isAuthorized(capabilities: Set[Capability], tokens: Set[Token]): Boolean =
    capabilities.forall(_.authorize(tokens))

  def notAuthorizedResult[B](implicit authorizedRequest: AuthorizedRequestWithIdentity[B]): Future[Result]

}
