package controllers

import models.{Purchase, Logic, DBAccess}
import play.api._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsPath, Reads, Json}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

class Application extends Controller {

  def purchases(username: Option[String]) = Action { request =>
    username match {
      case Some(username: String) =>
        val list = Logic.getPurchases(username)
        Ok(views.html.purchases(username, list))

      case _ =>
        Logic.getLoginBySessionID(request.session.get("session_id")) match {
          case login: String =>
            val list = Logic.getPurchases(login)
            Ok(views.html.purchases(login, list))
          case _ =>
            Ok(views.html.login("Login")).withNewSession
        }
    }
  }

  def index = TODO

  def documentation= Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getLoginPage = Action { request =>
    Logic.getLoginBySessionID(request.session.get("session_id")) match {
      case login: String =>
        Ok(views.html.purchases(login, Logic.getPurchases(login)))
      case _ =>
        Ok(views.html.login("Login")).withNewSession
    }
  }

  def getSignupPage = Action { request =>
    Logic.getLoginBySessionID(request.session.get("session_id")) match {
      case login: String => BadRequest("You are already authorised")
      case _ => Ok(views.html.signup("Sign up page"))
    }
  }

  def signup = Action(BodyParsers.parse.json) { implicit request =>
    Logic.getLoginBySessionID(request.session.get("session_id")) match {
      case login: String =>
        Ok(Application.alreadyAuthorised)
      case _ =>
        val userData = request.body.validate[Application.SignupData]
        userData.fold(
          error => BadRequest(Application.notAuthorised),
          signupData => {
            Logic.createUser(signupData) match {
              case Some(session: String) =>
                Ok(Json.obj("code" -> "success"))
                  .withSession(request.session + ("session_id" -> session))
              case _ => BadRequest
            }
          }
        )
    }
  }

  def login = Action { implicit request =>
    val loginData = Application.getLoginData.bindFromRequest.get
    if (Logic.auth(loginData.login, loginData.pass))
      Logic.login(loginData.login) match {
        case Some(session: String) =>
          Redirect(routes.Application.purchases(None))
            .withSession(request.session + ("session_id" -> session))
        case _ =>
          Ok(views.html.error("Login", "You are already authorised"))
      }
    else
      Ok(views.html.error("Login", "Wrong pass or login"))
  }

  def logout = Action { request =>
    Logic.getLoginBySessionID(request.session.get("session_id")) match {
      case login: String =>
        Logic.logout(login)
        Redirect(routes.Application.index())
      case _ =>
        Unauthorized(views.html.login("Login")).withNewSession
    }
  }

  def savePurchase = Action { implicit request =>
    Logic.getLoginBySessionID(request.session.get("session_id")) match {
      case login: String =>
        val userData = Application.getPurchaseForm.bindFromRequest.get
        Logic.savePurchase(new Purchase(userData.product, userData.amount, login, userData.groupID))
        Redirect(routes.Application.purchases(None))
      case _ =>
        Unauthorized(views.html.login("Login")).withNewSession
    }
  }

  def savePurchaseJSON = Action(BodyParsers.parse.json) { implicit request =>
    Logic.getLoginBySessionID(request.session.get("session_id")) match {
      case login: String =>
        val userData = request.body.validate[Application.PurchaseData]
        userData.fold(
          error => BadRequest(Application.notAuthorised),
          purchaseData => {
            val purchase = new Purchase(purchaseData.product, purchaseData.amount, login, purchaseData.groupID)
            Logic.savePurchase(purchase)
            Ok(purchase.toJson)
          }
        )
      case _ =>
        Ok(Application.notAuthorised).withNewSession
    }
  }

  def groupInfo(id: Int) = Action {
    val list = Logic.getUsersInGroup(id)
    if (list.isEmpty) {
      if (id == 0) {
        Ok(views.html.error("Group info", "You haven't specified group id"))
      } else {
        Ok(views.html.error("Group info", "No group with id: " + id))
      }
    } else {
      Ok(views.html.group("Group info", id, list))
    }
  }

  val array = Array(
    "#F7464A",
    "#46BFBD",
    "#FDB45C",
    "#A716AA",
    "#B6677C",
    "#D24AC5"
  )

  def getGroupPieData(groupID: Int) = Action { request =>
    Logic.getLoginBySessionID(request.session.get("session_id")) match {
      case login: String =>
        var last = -1
        Ok(Json.toJson(Logic.getGroupedProductFromGroup(login, 1).map(pur => {
          last = (last + 1) % array.length
          Json.obj(
            "value" -> pur.amount,
            "label" -> pur.productName,
            "color" -> array(last),
            "highlight" -> "#DFC870"
          )
        }
        )))
      case _ => Ok(Application.notAuthorised)
    }
  }
}

object Application {
  val notAuthorisedCode = 1
  val alreadyAuthorisedCode = 2

  implicit val purchaseReads: Reads[PurchaseData] = (
    (JsPath \ "product") .read[String] and
      (JsPath \ "amount").read[Int] and
      (JsPath \ "groupID").read[Int]
    )(PurchaseData.apply _)

  case class PurchaseData(product: String, amount: Int, groupID: Int)
  def getPurchaseForm = {
    Form(
      mapping(
        "product" -> text,
        "amount" -> number,
        "groupID" -> number
      )(PurchaseData.apply)(PurchaseData.unapply)
    )
  }

  case class LoginData(login: String, pass: String)
  def getLoginData = {
    Form(
      mapping(
        "login" -> text,
        "password" -> text
      )(LoginData.apply)(LoginData.unapply)
    )
  }

  implicit val loginReads: Reads[LoginData] = (
    (JsPath \ "login") .read[String] and
      (JsPath \ "password").read[String]
    )(LoginData.apply _)

  case class SignupData(login: String, password: String, name: String, surname: String, email: String)
  def getSignUpData = {
    Form(
      mapping(
        "login" -> text,
        "password" -> text,
        "name" -> text,
        "surname" -> text,
        "email" -> text
      )(SignupData.apply)(SignupData.unapply)
    )
  }

  implicit val signupReads: Reads[SignupData] = (
    (JsPath \ "login") .read[String] and
      (JsPath \ "password").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "surname").read[String] and
      (JsPath \ "email").read[String]
    )(SignupData.apply _)

  def notAuthorised = {
    Json.obj(
      "error" -> "Not authorised",
      "code" -> notAuthorisedCode
    )
  }

  def alreadyAuthorised = {
    Json.obj(
      "error" -> "Already authorised",
      "code" -> alreadyAuthorisedCode
    )
  }

  def getSessionID(request: Request[AnyContent]) = {
    request.session.get("session_id")
  }
}