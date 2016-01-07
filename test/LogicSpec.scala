import models.{Login, DBService, Logic}
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec

import org.mockito.Mockito._
/**
  * Created by neikila on 07.01.16.
  */
class LogicSpec extends PlaySpec with MockitoSugar {

  "Logic#auth" should {
    "return true if login and password are correct" in {
      val loginData = new Login("testLogin", "testPassword")
      val mockDB = mock[DBService]
      when(mockDB.getLoginData(loginData.login)) thenReturn Some(loginData)

      val logic = new Logic() {
        override val db = mockDB
      }

      val authResult = logic.auth(loginData.login, loginData.password)
      authResult mustBe defined
    }

    "return false if login is wrong" in {
      val loginData = new Login("testLogin", "testPassword")
      val wrongLogin = "wrongLogin"

      val mockDB = mock[DBService]
      when(mockDB.getLoginData(loginData.login)) thenReturn Some(loginData)

      val logic = new Logic() {
        override val db = mockDB
      }

      val authResult = logic.auth(wrongLogin, loginData.password)
      authResult mustBe None
    }

    "return false if password is wrong" in {
      val loginData = new Login("testLogin", "testPassword")
      val wrongPassword = "wrongPassword"

      val mockDB = mock[DBService]
      when(mockDB.getLoginData(loginData.login)) thenReturn Some(loginData)

      val logic = new Logic() {
        override val db = mockDB

      }

      val authResult = logic.auth(loginData.login, wrongPassword)
      authResult mustBe None
    }

    "return different session for double login" in {
      val loginData = new Login("testLogin", "testPassword")

      val mockDB = mock[DBService]
      when(mockDB.getLoginData(loginData.login)) thenReturn Some(loginData)

      val logic = new Logic() {
        override val db = mockDB
      }

      val authFirstResult = logic.auth(loginData.login, loginData.password)
      val authSecondResult = logic.auth(loginData.login, loginData.password)

      authSecondResult mustBe defined
      authFirstResult must not be authSecondResult
    }
  }
}
