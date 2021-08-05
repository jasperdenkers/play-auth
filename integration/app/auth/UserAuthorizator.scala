package auth

import auth.Tokens.IsAdmin
import com.jasperdenkers.play.auth.Authorizator
import models.User
import play.api.mvc._

import scala.concurrent.Future

class UserAuthorizator extends Authorizator[User] with Results {

  def getTokens[B](authenticatedRequest: AuthenticatedRequestWithIdentity[B]) =
    Future.successful {
      authenticatedRequest.identity match {
        case admin if admin.username == "admin" => Set(IsAdmin)
        case _ => Set.empty
      }
    }

  def notAuthorizedResult[B](implicit authorizedRequest: AuthorizedRequestWithIdentity[B]) =
    Future.successful {
      Unauthorized(views.html.unauthorized())
    }

}