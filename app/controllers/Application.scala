package controllers

import controllers.Application.PurchaseData
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

  def index = Action {
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

  def login = Action { implicit request =>
    val loginData = Application.getLoginData.bindFromRequest.get
    if (Logic.auth(loginData.login, loginData.pass))
      Ok(views.html.purchases(loginData.login, Logic.getPurchases(loginData.login)))
        .withSession(request.session + ("session_id" -> Logic.createSessionID(loginData.login)))
    else
      Ok(views.html.error("Login", "Wrong pass or login"))
  }

  def logout = Action {
    Redirect(routes.Application.index()).withNewSession
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
}

object Application {
  val notAuthorisedCode = 1
  implicit val placeReads: Reads[PurchaseData] = (
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

  def notAuthorised = {
    Json.obj(
      "error" -> "Not authorised",
      "code" -> notAuthorisedCode
    )
  }
}