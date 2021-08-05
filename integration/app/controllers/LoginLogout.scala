package controllers

import javax.inject.Inject

import auth.UserAuthenticator
import com.jasperdenkers.play.auth.{Authentication, LoginData}
import models.User
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LoginLogout @Inject()(val authenticator: UserAuthenticator) extends InjectedController with Authentication[User] {

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "remember" -> boolean
    )(LoginData.apply)(LoginData.unapply)
  )

  def login = MaybeAuthenticated { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def doLogin = Action.async { implicit request =>
    val formBinding = loginForm.bindFromRequest

    formBinding.fold(
      formWithErrors =>
        Future.successful {
          BadRequest(views.html.login(formWithErrors))
        },
      loginData =>
        authenticator.login(loginData).map {
          case Some(userSessionCookie) =>
            val redirectAfterLogin = Redirect(controllers.routes.WithAuthentication.authenticated)

            redirectAfterLogin.withCookies(userSessionCookie).flashing {
              "success" -> "You are successfully logged in"
            }
          case None =>
            val formWithAuthError = formBinding.withGlobalError("Invalid login credentials provided")

            BadRequest(views.html.login(formWithAuthError))
        }
    )
  }

  def doLogout = Action.async { request =>
    val redirectAfterLogout = Redirect(controllers.routes.LoginLogout.login).flashing {
      "success" -> "You are successfully logged out"
    }

    authenticator.logout(request, redirectAfterLogout)
  }

}
