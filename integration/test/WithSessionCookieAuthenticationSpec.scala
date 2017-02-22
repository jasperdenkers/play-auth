import auth.UserAuthenticator
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.test.Helpers._
import play.api.test._

class WithSessionCookieAuthenticationSpec extends PlaySpec with OneAppPerSuite with AuthenticationHelper {

  val userAuthenticator = app.injector.instanceOf[UserAuthenticator]

  "WithAuthentication controller" should {

    val userAuthenticator = app.injector.instanceOf[UserAuthenticator]

    "update the session on every request" in {
      val sessionCookie1 = getSessionCookie(userLoginData(false))
      val session1 = userAuthenticator.sessionFromCookie(sessionCookie1)

      session1.requestCount mustBe 0

      val result1 = route(app, FakeRequest(controllers.routes.WithSessionCookieAuthentication.authenticated()).withCookies(sessionCookie1.get)).get

      val sessionCookie2 = cookies(result1).get(userAuthenticator.cookieName)
      val session2 = userAuthenticator.sessionFromCookie(sessionCookie2)

      session2.requestCount mustBe 1

      val result2 = route(app, FakeRequest(controllers.routes.WithSessionCookieAuthentication.authenticated()).withCookies(sessionCookie2.get)).get

      val sessionCookie3 = cookies(result2).get(userAuthenticator.cookieName)
      val session3 = userAuthenticator.sessionFromCookie(sessionCookie3)

      session3.requestCount mustBe 2
    }

  }

}