package com.jasperdenkers.play.auth

import org.scalatestplus.play._

class CapabilitySpec extends PlaySpec {

  "Capabilities" must {

    object TokenA extends Token
    object TokenB extends Token

    object CapabilityByTokenA extends CapabilityByToken {
      def predicate = _ == TokenA
    }
    object CapabilityByTokenB extends CapabilityByToken {
      def predicate = _ == TokenB
    }

    "be composable into conjuctions with the implicit && operator" in {
      (CapabilityByTokenA && CapabilityByTokenB) mustBe Conjunction(CapabilityByTokenA, CapabilityByTokenB)
    }

    "be composable into disjunctions with the implicit || operator" in {
      (CapabilityByTokenA || CapabilityByTokenB) mustBe Disjunction(CapabilityByTokenA, CapabilityByTokenB)
    }

  }

}