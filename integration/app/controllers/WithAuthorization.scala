package controllers

import javax.inject.Inject

import auth.Capabilities.Admin
import auth.{UserAuthenticator, UserAuthorizator}
import com.jasperdenkers.play.auth.{Authentication, Authorization}
import models.User
import play.api.mvc._

class WithAuthorization @Inject()(val authenticator: UserAuthenticator, val authorizator: UserAuthorizator) extends InjectedController with Authentication[User] with Authorization[User] {

  def authorizedAdmin = Authorized(Admin) { implicit request =>
    Ok(views.html.authorizedAdmin())
  }

  def maybeAuthorized = MaybeAuthorized { implicit request =>
    Ok(views.html.maybeAuthorized())
  }

}
