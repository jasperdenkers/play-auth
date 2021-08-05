package controllers

import javax.inject.Inject

import auth.{UserAuthenticator, UserAuthorizator}
import com.jasperdenkers.play.auth.{Authentication, Authorization}
import models.User
import play.api.mvc._

class Application @Inject() (val authenticator: UserAuthenticator, val authorizator: UserAuthorizator)
    extends InjectedController
    with Authentication[User]
    with Authorization[User] {

  def index = MaybeAuthorized { implicit request =>
    Ok(views.html.index())
  }

}
