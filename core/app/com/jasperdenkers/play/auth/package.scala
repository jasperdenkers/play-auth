package com.jasperdenkers.play

import play.api.mvc.RequestHeader

package object auth {

  type TokenPredicate = Token => Boolean

  implicit class CapabilityDisjunctionOps(firstCapability: Capability) {
    def ||(secondCapability: Capability) = Disjunction(firstCapability, secondCapability)
  }

  implicit class CapabilityConjunctionOps(firstCapability: Capability) {
    def &&(secondCapability: Capability) = Conjunction(firstCapability, secondCapability)
  }

  implicit def token2Capability(token: Token): Capability = new CapabilityByToken {
    def predicate = _ == token
  }

  implicit def isAuthenticated(implicit request: RequestHeader) =
    request match {
      case _: AuthenticatedRequest[_, _] | _: AuthorizedRequest[_, _] => true
      case _                                                          => false
    }

  implicit def isAuthorized(capabilities: Capability*)(implicit request: RequestHeader) =
    request match {
      case authorizedRequest: AuthorizedRequest[_, _] => authorizedRequest.isAuthorized(capabilities.toSet)
      case _                                          => false
    }

}
