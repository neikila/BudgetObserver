package controllers

import models.{User, Purchase, Logic, DBAccess}
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
        Logic.getLoginBySessionID(Utils.getSessionID(request)) match {
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

  def savePurchase = Action { implicit request =>
    Logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        val userData = Application.getPurchaseForm.bindFromRequest.get
        Logic.savePurchase(new Purchase(userData.product, userData.amount, login, userData.groupID))
        Redirect(routes.Application.purchases(None))
      case _ =>
        Unauthorized(views.html.login("Login")).withNewSession
    }
  }

  def savePurchaseJSON = Action(BodyParsers.parse.json) { implicit request =>
    request.body.validate[Application.PurchaseData].fold(
      error => BadRequest(ErrorMessage.notAuthorised),
      purchaseData => {
        Logic.getLoginBySessionID(Utils.getSessionID(request, purchaseData)) match {
          case login: String =>
            val purchase = new Purchase(purchaseData.product, purchaseData.amount, login, purchaseData.groupID)
            Logic.savePurchase(purchase)
            Ok(purchase.toJson)
          case _ =>
            Ok(ErrorMessage.notAuthorised).withNewSession
        }
      }
    )
  }

  def groupInfo(id: Int) = Action {
    Logic.getUsersInGroup(id) match {
      case Some(list: List[User]) => Ok(views.html.group("Group info", id, list))
      case _ =>
          Ok(views.html.error("Group info", {
            if (id == 0) "You haven't specified group id"
            else "No group with id: " + id
          }))
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
    Logic.getLoginBySessionID(Utils.getSessionID(request)) match {
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
      case _ => Ok(ErrorMessage.notAuthorised)
    }
  }
}

object Application {

  case class PurchaseData(product: String, amount: Int, groupID: Int, override val sessionID: Option[String] = None) extends IncomeData

  def getPurchaseForm = {
    Form(
      mapping(
        "product" -> text,
        "amount" -> number,
        "groupID" -> number,
        Utils.session_tag -> optional(text)
      )(PurchaseData.apply)(PurchaseData.unapply)
    )
  }

  implicit val purchaseReads: Reads[PurchaseData] = (
    (JsPath \ "product") .read[String] and
      (JsPath \ "amount").read[Int] and
      (JsPath \ "groupID").read[Int] and
      (JsPath \ Utils.session_tag).readNullable[String]
    )(PurchaseData.apply _)
}