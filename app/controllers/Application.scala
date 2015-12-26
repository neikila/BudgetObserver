package controllers

import models.{Purchase, Logic, DBAccess}
import play.api._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._


class Application extends Controller {
  def purchases(username: String = "test") = Action {
    val list = Logic.getPurchases(username)
    Ok(views.html.purchases(username, list))
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
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
}