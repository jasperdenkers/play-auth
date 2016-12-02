package controllers

import javax.inject.Inject

import auth.{Authentication, UserAuthenticator}
import models.User
import play.api.mvc._

class WithAuthentication @Inject()(val authenticator: UserAuthenticator) extends Controller with Authentication[User] {

  def authenticated = Authenticated { implicit request =>
    Ok(views.html.authenticated())
  }

  def maybeAuthenticated = MaybeAuthenticated { implicit request =>
    Ok(views.html.maybeAuthenticated())
  }

}
