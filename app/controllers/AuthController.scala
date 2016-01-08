package controllers

import models.Logic
import play.api._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Reads
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by neikila on 28.12.15.
  */
class AuthController extends Controller {
  val logic = Logic()

  def getLoginPage = Action { request =>
    logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        Redirect(routes.Application.purchases)
      case _ =>
        Ok(views.html.auth.login("Login")).withNewSession
    }
  }

  def getSignupPage = Action { request =>
    logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String => BadRequest("You are already authorised")
      case _ => Ok(views.html.auth.signup("Sign up page"))
    }
  }

  def signup = Action(BodyParsers.parse.json) { implicit request =>
    request.body.validate[AuthController.SignupData].fold(
      error => BadRequest(ErrorMessage.wrongFormat),
      signupData => {
        logic.getLoginBySessionID(Utils.getSessionID(request, signupData)) match {
          case login: String =>
            Ok(ErrorMessage.alreadyAuthorised)
          case _ =>
            logic.createUser(signupData) match {
              case Some("UserExist") => Ok(ErrorMessage.suchUserExist)
              case Some("PasswordsDiffer") => Ok(ErrorMessage.passwordsDiffer)
              case Some(session: String) =>
                Ok(JSONResponse.success)
                  .withSession(request.session + (Utils.session_tag -> session))
              case _ => BadRequest(ErrorMessage.errorWhileHandlingRequest)
            }
        }
      }
    )
  }

  def loginJSON = Action(BodyParsers.parse.json) { request =>
    request.body.validate[AuthController.LoginData].fold(
      errors => {
        var extraMessage = ""
        for (error <- errors) {
          extraMessage += (" " + error._1)
        }
        Ok(ErrorMessage.wrongFormat(extraMessage))
      },
      loginData =>
        logic.getLoginBySessionID(request.session.get(Utils.session_tag)) match {
          case login: String =>
            Ok(ErrorMessage.alreadyAuthorised)
          case _ =>
            logic.auth(loginData.login, loginData.pass) match {
              case Some(session: String) =>
                Ok(JSONResponse.success)
                  .withSession(request.session + (Utils.session_tag -> session))
              case _ => Ok(ErrorMessage.wrongAuth)
            }
        }
    )
  }

  def logout = Action { request =>
    logic.getLoginBySessionID(request.session.get(Utils.session_tag)) match {
      case login: String =>
        logic.logout(login)
      case _ =>
    }
    Redirect(routes.Application.index).withNewSession
  }
}


object AuthController {
  case class LoginData(login: String, pass: String, override val sessionID: Option[String]) extends IncomeData
  def getLoginData = {
    Form(
      mapping(
        "login" -> text,
        "password" -> text,
        Utils.session_tag -> optional(text)
      )(LoginData.apply)(LoginData.unapply)
    )
  }

  implicit val loginReads: Reads[LoginData] = (
    (JsPath \ "login") .read[String].filter((login: String) => login.length > 3) and
      (JsPath \ "password").read[String] and
      (JsPath \ Utils.session_tag) .readNullable[String]
    )(LoginData.apply _)

  case class SignupData(login: String, password: String, password_repeat: String,
                        name: String, surname: String, email: String,
                        override val sessionID: Option[String]) extends IncomeData
  def getSignUpData = {
    Form(
      mapping(
        "login" -> text,
        "password" -> text,
        "password_repeat" -> text,
        "name" -> text,
        "surname" -> text,
        "email" -> text,
        Utils.session_tag -> optional(text)
      )(SignupData.apply)(SignupData.unapply)
    )
  }

  implicit val signupReads: Reads[SignupData] = (
    (JsPath \ "login") .read[String] and
      (JsPath \ "password").read[String] and
      (JsPath \ "password_repeat").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "surname").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ Utils.session_tag).readNullable[String]
    )(SignupData.apply _)
}