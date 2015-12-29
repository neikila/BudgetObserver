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
  def getLoginPage = Action { request =>
    Logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        Ok(views.html.purchases(login, Logic.getPurchases(login)))
      case _ =>
        Ok(views.html.login("Login")).withNewSession
    }
  }

  def getSignupPage = Action { request =>
    Logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String => BadRequest("You are already authorised")
      case _ => Ok(views.html.signup("Sign up page"))
    }
  }

  def signup = Action(BodyParsers.parse.json) { implicit request =>
    Logic.getLoginBySessionID(request.session.get(Utils.session_tag)) match {
      case login: String =>
        Ok(ErrorMessage.alreadyAuthorised)
      case _ =>
        val userData = request.body.validate[AuthController.SignupData]
        userData.fold(
          error => BadRequest(ErrorMessage.notAuthorised),
          signupData => {
            Logic.createUser(signupData) match {
              case Some(session: String) =>
                Ok(Json.obj("code" -> "success"))
                  .withSession(request.session + (Utils.session_tag -> session))
              case _ => BadRequest(ErrorMessage.errorWhileHandlingRequest)
            }
          }
        )
    }
  }

  def login = Action { implicit request =>
    val loginData = AuthController.getLoginData.bindFromRequest.get
    if (Logic.auth(loginData.login, loginData.pass))
      Logic.login(loginData.login) match {
        case Some(session: String) =>
          Redirect(routes.Application.purchases(None))
            .withSession(request.session + (Utils.session_tag -> session))
        case _ =>
          Ok(views.html.error("Login", "You are already authorised"))
      }
    else
      Ok(views.html.error("Login", "Wrong pass or login"))
  }

  def logout = Action { request =>
    Logic.getLoginBySessionID(request.session.get(Utils.session_tag)) match {
      case login: String =>
        Logic.logout(login)
        Redirect(routes.Application.index())
      case _ =>
        Unauthorized(views.html.login("Login")).withNewSession
    }
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
    (JsPath \ "login") .read[String] and
      (JsPath \ "password").read[String] and
      (JsPath \ Utils.session_tag) .readNullable[String]
    )(LoginData.apply _)

  case class SignupData(login: String, password: String, name: String, surname: String, email: String, override val sessionID: Option[String]) extends IncomeData
  def getSignUpData = {
    Form(
      mapping(
        "login" -> text,
        "password" -> text,
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
      (JsPath \ "name").read[String] and
      (JsPath \ "surname").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ Utils.session_tag).readNullable[String]
    )(SignupData.apply _)
}