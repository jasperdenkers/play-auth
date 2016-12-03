package auth

import javax.inject.Inject

import auth.Tokens.IsAdmin
import com.jasperdenkers.play.auth.Authorizator
import models.User
import play.api.Configuration
import play.api.libs.crypto.CookieSignerProvider
import play.api.mvc._

import scala.concurrent.Future

class UserAuthorizator @Inject()(configuration: Configuration, cookieSignerProvider: CookieSignerProvider) extends UserAuthenticator(configuration, cookieSignerProvider) with Authorizator[User] with Results {

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