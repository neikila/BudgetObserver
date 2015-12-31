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
        Redirect(routes.Application.purchases)
//        Ok(views.html.app.purchases(login, Logic.getPurchases(login)))
      case _ =>
        Ok(views.html.auth.login("Login")).withNewSession
    }
  }

  def getSignupPage = Action { request =>
    Logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String => BadRequest("You are already authorised")
      case _ => Ok(views.html.auth.signup("Sign up page"))
    }
  }

  def signup = Action(BodyParsers.parse.json) { implicit request =>
    request.body.validate[AuthController.SignupData].fold(
      error => BadRequest(ErrorMessage.wrongFormat),
      signupData => {
        Logic.getLoginBySessionID(Utils.getSessionID(request, signupData)) match {
          case login: String =>
            Ok(ErrorMessage.alreadyAuthorised)
          case _ =>
            Logic.createUser(signupData) match {
              case Some(session: String) =>
                Ok(JSONResponse.success)
                  .withSession(request.session + (Utils.session_tag -> session))
              case Some(false) => Ok(ErrorMessage.suchUserExist)
              case _ => BadRequest(ErrorMessage.errorWhileHandlingRequest)
            }
        }
      }
    )
  }

  def login = Action { implicit request =>
    val loginData = AuthController.getLoginData.bindFromRequest.get
    if (Logic.auth(loginData.login, loginData.pass))
      Logic.login(loginData.login) match {
        case Some(session: String) =>
          Redirect(routes.Application.purchases)
            .withSession(request.session + (Utils.session_tag -> session))
        case _ =>
          Ok(views.html.incl.error("Login", "You are already authorised"))
      }
    else
      Ok(views.html.incl.error("Login", "Wrong pass or login"))
  }

  def loginJSON = Action(BodyParsers.parse.json) { request =>
    request.body.validate[AuthController.LoginData].fold(
      error => Ok(ErrorMessage.wrongFormat),
      loginData =>
        if (Logic.auth(loginData.login, loginData.pass))
          Logic.login(loginData.login) match {
            case Some(session: String) =>
              Ok(JSONResponse.success)
                .withSession(request.session + (Utils.session_tag -> session))
            case _ => Ok(ErrorMessage.alreadyAuthorised)
          }
        else
          Ok(ErrorMessage.wrongAuth)
    )
  }

  def logout = Action { request =>
    Logic.getLoginBySessionID(request.session.get(Utils.session_tag)) match {
      case login: String =>
        Logic.logout(login)
        Redirect(routes.Application.index)
      case _ =>
        Unauthorized(views.html.auth.login("Login")).withNewSession
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