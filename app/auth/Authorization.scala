package auth

import play.api.mvc.{ActionBuilder, ActionFunction, Request, Result}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Authorization[A] extends Authentication[A] {

  def authorizator: Authorizator[A]

  def authenticator = authorizator

  type AuthorizedRequestWithIdentity[B] = AuthorizedRequest[A, B]

  def onUnauthorized[B](authorizedRequest: AuthorizedRequestWithIdentity[B]): Future[Result] = authorizator.notAuthorizedResult(authorizedRequest)

  def Authorized(capabilities: Capability*) = Authenticated andThen new ActionFunction[AuthenticatedRequestWithIdentity, AuthorizedRequestWithIdentity] {
    def invokeBlock[B](authenticatedRequest: AuthenticatedRequestWithIdentity[B], block: (AuthorizedRequestWithIdentity[B]) => Future[Result]) =
      authorizator.getTokens(authenticatedRequest).flatMap { tokens =>
        val authorizedRequest = AuthorizedRequest.fromAuthenticatedRequest(authenticatedRequest, tokens)

        if (authorizedRequest.isAuthorized(capabilities.toSet))
          block(authorizedRequest)
        else
          onUnauthorized(authorizedRequest)
      }
  }

  def MaybeAuthorized = MaybeAuthenticated andThen new ActionBuilder[Request] {
    def invokeBlock[B](request: Request[B], block: (Request[B]) => Future[Result]) = {
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
