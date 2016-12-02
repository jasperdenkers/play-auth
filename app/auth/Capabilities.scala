package auth

import auth.Tokens.IsAdmin

object Capabilities {

  case object Admin extends CapabilityByToken {
    def predicate = _ == IsAdmin
  }

}