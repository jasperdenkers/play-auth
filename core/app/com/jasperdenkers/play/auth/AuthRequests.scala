package com.jasperdenkers.play.auth

trait AuthRequests[A] {

  type AuthenticatedRequestWithIdentity[B] = AuthenticatedRequest[A, B]
  type AuthorizedRequestWithIdentity[B] = AuthorizedRequest[A, B]

}
