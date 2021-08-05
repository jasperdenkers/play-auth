import auth.UserAuthenticator
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers._

class LoginLogoutSpec extends PlaySpec with GuiceOneAppPerSuite with AuthenticationHelper {

  val userAuthenticator = app.injector.instanceOf[UserAuthenticator]

  "LoginLogout controller" should {

    "successfully authenticate admin and return a cookie with the session" in {
      val loginData     = adminLoginData(false)
      val result        = loginResult(loginData)
      val sessionCookie = cookies(result).get(userAuthenticator.cookieName)
      val session       = userAuthenticator.SessionCookieBaker.decodeFromCookie(sessionCookie)

      status(result) mustBe SEE_OTHER
      session.username mustBe loginData.identifier
    }

    "give an error when logging in with invalid credentials" in {
      val result = loginResult(invalidLoginData)

      status(result) mustBe BAD_REQUEST
    }

  }

}
