package com.jasperdenkers.play.auth

trait Capability {
  def authorize(tokens: Set[Token]): Boolean
}

trait CapabilityByToken extends Capability {
  def predicate: TokenPredicate
  override def authorize(tokens: Set[Token]) = tokens.exists(predicate)
}

trait Authorized extends Capability {
  def authorize(tokens: Set[Token]) = true
}

case object Authorized extends Authorized

trait NotAuthorized extends Capability {
  def authorize(tokens: Set[Token]) = false
}

case object NotAuthorized extends NotAuthorized

case class Disjunction(capabilities: Set[Capability]) extends Capability {
  def authorize(tokens: Set[Token]) = capabilities.exists(_.authorize(tokens))
}

object Disjunction {

  def apply(capabilities: Capability*) = new Disjunction(capabilities.toSet)

}

case class Conjunction(capabilities: Set[Capability]) extends Capability {
  def authorize(tokens: Set[Token]) = capabilities.forall(_.authorize(tokens))
}

object Conjunction {

  def apply(capabilities: Capability*) = new Conjunction(capabilities.toSet)

}

case class Not(capability: Capability) extends Capability {
  def authorize(tokens: Set[Token]) = !capability.authorize(tokens)
}