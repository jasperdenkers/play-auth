import auth.UserAuthenticator
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers._
import play.api.test._

class WithAuthenticationSpec extends PlaySpec with GuiceOneAppPerSuite with AuthenticationHelper {

  val userAuthenticator = app.injector.instanceOf[UserAuthenticator]

  "WithAuthentication controller" should {

    val userAuthenticator = app.injector.instanceOf[UserAuthenticator]

    "redirect to the login page on accessing a page that requires authentication without being authenticated" in {
      val result = route(app, FakeRequest(controllers.routes.WithAuthentication.authenticated())).get

      status(result) mustBe SEE_OTHER
    }

    "successfully return a page that requires authentication while being authenticated" in {
      val sessionCookie = getSessionCookie(userLoginData(false))
      val result = route(app, FakeRequest(controllers.routes.WithAuthentication.authenticated()).withCookies(sessionCookie.get)).get

      status(result) mustBe OK
    }

  }

}