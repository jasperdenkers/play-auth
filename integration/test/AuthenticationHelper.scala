import auth.UserAuthenticator
import com.jasperdenkers.play.auth.LoginData
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

trait AuthenticationHelper { self: GuiceOneAppPerSuite =>

  def userAuthenticator: UserAuthenticator

  def adminLoginData(remember: Boolean) = LoginData("admin", "123", remember)
  def userLoginData(remember: Boolean)  = LoginData("user", "321", remember)
  val invalidLoginData                  = LoginData("invalid", "invalid", false)

  def loginResult(loginData: LoginData) = {
    val data = Map(
      "username" -> loginData.identifier,
      "password" -> loginData.plaintextPassword,
      "remember" -> loginData.remember.toString
    )

    route(app, FakeRequest(controllers.routes.LoginLogout.doLogin).withJsonBody(Json.toJson(data))).get
  }

  def getSessionCookie(loginData: LoginData) = {
    val result = loginResult(loginData)

    cookies(result).get(userAuthenticator.cookieName)
  }

}
