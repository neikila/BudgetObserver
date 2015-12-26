package controllers

import models.{Purchase, Logic, DBAccess}
import play.api._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._


class Application extends Controller {
  def purchases(username: String = "test1") = Action {
    val list = Logic.getPurchases(username)
    Ok(views.html.purchases(username, list))
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getLoginPage = Action { request =>
    request.session.get("session_id").map { sessionID =>
      val result = Logic.getLoginBySessionID(sessionID)
      result match {
        case login: String => Ok(views.html.purchases(login, Logic.getPurchases(login)))
        case _             => Ok(views.html.login("Login")).withNewSession
      }
    }.getOrElse {
      Ok(views.html.login("Login"))
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
    val userData = Application.getPurchaseForm.bindFromRequest.get
    Logic.savePurchase(new Purchase(userData.product, userData.amount, "test", 1))
    Redirect(routes.Application.purchases())
  }

  def savePurchaseJSON = Action { implicit request =>
    val userData = Application.getPurchaseForm.bindFromRequest.get
    val purchase = new Purchase(userData.product, userData.amount, "test", 1)
    Logic.savePurchase(purchase)
    Ok(purchase.toJson.toString())
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
  case class PurchaseData(product: String, amount: Int)
  def getPurchaseForm = {
    Form(
      mapping(
        "product" -> text,
        "amount" -> number
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
}