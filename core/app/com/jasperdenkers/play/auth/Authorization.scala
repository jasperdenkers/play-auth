package com.jasperdenkers.play.auth

import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Authorization[A] extends AuthRequests[A] { self: Authentication[A] with BaseController =>

  def authorizator: Authorizator[A]

  def onUnauthorized[B](authorizedRequest: AuthorizedRequestWithIdentity[B]): Future[Result] =
    authorizator.notAuthorizedResult(authorizedRequest)

  def Authorized(
      authentication: BaseActionBuilder[AuthenticatedRequestWithIdentity]
  )(capabilities: Capability*): ActionBuilder[AuthorizedRequestWithIdentity, AnyContent] =
    authentication andThen new ActionFunction[AuthenticatedRequestWithIdentity, AuthorizedRequestWithIdentity] {
      def executionContext = self.defaultExecutionContext
      def invokeBlock[B](
          authenticatedRequest: AuthenticatedRequestWithIdentity[B],
          block: AuthorizedRequestWithIdentity[B] => Future[Result]
      ) =
        authorizator.getTokens(authenticatedRequest).flatMap { tokens =>
          val authorizedRequest = AuthorizedRequest.fromAuthenticatedRequest(authenticatedRequest, tokens)

          if (authorizedRequest.isAuthorized(capabilities.toSet))
            block(authorizedRequest)
          else
            onUnauthorized(authorizedRequest)
        }
    }

  def Authorized(capabilities: Capability*): ActionBuilder[AuthorizedRequestWithIdentity, AnyContent] =
    Authorized(Authenticated)(capabilities: _*)

  def MaybeAuthorized = MaybeAuthenticated andThen new BaseActionBuilder[Request] {
    def invokeBlock[B](request: Request[B], block: Request[B] => Future[Result]) = {
      request match {
        case authenticatedRequest: AuthenticatedRequestWithIdentity[B @unchecked] =>
          authorizator.getTokens(authenticatedRequest).flatMap { tokens =>
            val authorizedRequest = AuthorizedRequest.fromAuthenticatedRequest(authenticatedRequest, tokens)

            block(authorizedRequest)
          }
        case _ => block(request)
      }
    }
  }

}
