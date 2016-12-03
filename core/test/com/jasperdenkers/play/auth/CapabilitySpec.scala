package com.jasperdenkers.play.auth

import org.scalatestplus.play._

class CapabilitySpec extends PlaySpec {

  "Capabilities" must {

    object tokenA extends Token
    object tokenB extends Token

    object capabilityByTokenA extends CapabilityByToken {
      def predicate = _ == tokenA
    }
    object capabilityByTokenB extends CapabilityByToken {
      def predicate = _ == tokenB
    }

    "be composable into conjuctions with implicit && operator" in {
      (capabilityByTokenA && capabilityByTokenB) mustBe Conjunction(capabilityByTokenA, capabilityByTokenB)
    }

    "be composable into disjunctions with implicit || operator" in {
      (capabilityByTokenA || capabilityByTokenB) mustBe Disjunction(capabilityByTokenA, capabilityByTokenB)
    }

  }

}