package auth

import auth.Tokens.IsAdmin
import com.jasperdenkers.play.auth.CapabilityByToken

object Capabilities {

  case object Admin extends CapabilityByToken {
    def predicate = _ == IsAdmin
  }

}
