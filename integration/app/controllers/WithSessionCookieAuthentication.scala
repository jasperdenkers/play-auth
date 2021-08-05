package controllers

import javax.inject.Inject

import auth.UserAuthenticator
import com.jasperdenkers.play.auth.SessionCookieAuthentication
import models.User
import play.api.mvc._

class WithSessionCookieAuthentication @Inject() (val authenticator: UserAuthenticator)
    extends InjectedController
    with SessionCookieAuthentication[User, auth.Session] {

  def authenticated = Authenticated { implicit request =>
    Ok(views.html.authenticated())
  }

  def maybeAuthenticated = MaybeAuthenticated { implicit request =>
    Ok(views.html.maybeAuthenticated())
  }

}
