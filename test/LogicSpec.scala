import controllers.AuthController.SignupData
import controllers.IncomeData
import models.{User, Login, DBService, logic}
import org.junit.runner.RunWith
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.mockito.Matchers._

import org.mockito.Mockito._
import org.specs2.runner.JUnitRunner

/**
  * Created by neikila on 07.01.16.
  */
@RunWith(classOf[JUnitRunner])
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

  "return SessionID if no problem while creating user" in {
    val pass = "testPass"
    val signupData = new SignupData  ("testLogin", pass, pass,
      "testName", "testSurname", "testEmail@test.test", None)

    val mockDB = mock[DBService]
    when(mockDB.getUser(signupData.login)) thenReturn None
    when(mockDB.saveUser(any[User])) thenReturn 0
    when(mockDB.saveLogin(any[Login])) thenReturn 0

    val logic = new Logic() {
      override val db = mockDB
    }

    val signupResult = logic.createUser(signupData)

    signupResult mustBe defined
    signupResult must not be Some("PasswordsDiffer")
    signupResult must not be Some("UserExist")
  }

  "return PasswordsDiffer if password not equal repeat_password" in {
    val pass = "testPass"
    val differentPass = "differentPass"
    val signupData = new SignupData  ("testLogin", pass, differentPass,
      "testName", "testSurname", "testEmail@test.test", None)

    val mockDB = mock[DBService]
    when(mockDB.getUser(signupData.login)) thenReturn None
    when(mockDB.saveUser(any[User])) thenReturn 0
    when(mockDB.saveLogin(any[Login])) thenReturn 0

    val logic = new Logic() {
      override val db = mockDB
    }

    val signupResult = logic.createUser(signupData)

    signupResult mustBe Some("PasswordsDiffer")
  }

  "return UserExist if user with such login already exist" in {
    val pass = "testPass"
    val signupData = new SignupData  ("testLogin", pass, pass,
      "testName", "testSurname", "testEmail@test.test", None)

    val mockDB = mock[DBService]
    when(mockDB.getUser(signupData.login)) thenReturn Some(
      new User(signupData.login, signupData.name, signupData.surname, signupData.email)
    )
    when(mockDB.saveUser(any[User])) thenReturn 0
    when(mockDB.saveLogin(any[Login])) thenReturn 0

    val logic = new Logic() {
      override val db = mockDB
    }

    val signupResult = logic.createUser(signupData)

    signupResult mustBe Some("UserExist")
  }
}
