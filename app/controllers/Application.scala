package controllers

import models.{User, Purchase, Logic}
import play.api._
import play.api.data._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsPath, Reads, Json}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

class Application extends Controller {
  val logic = Logic()

  def purchases = Action { request =>
    logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        val list = logic.getPurchases(login)
        val groupName = logic.getDefaultGroupName(login)
        Ok(views.html.app.purchases(login, list, groupName))
      case _ =>
        Redirect(routes.AuthController.getLoginPage).withNewSession
    }
  }

  def index = Action {
    Ok(views.html.app.index())
  }

  def documentation= Action {
    Ok(views.html.app.doc("Your new application is ready."))
  }

  def savePurchase = Action { implicit request =>
    logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        val userData = Application.getPurchaseForm.bindFromRequest.get
        logic.savePurchase(new Purchase(userData.product, userData.amount, login, userData.groupID))
        Redirect(routes.Application.purchases)
      case _ =>
        Unauthorized(views.html.auth.login("Login")).withNewSession
    }
  }

  def savePurchaseJSON = Action(BodyParsers.parse.json) { implicit request =>
    request.body.validate[Application.PurchaseData].fold(
      error => BadRequest(ErrorMessage.notAuthorised),
      purchaseData => {
        logic.getLoginBySessionID(Utils.getSessionID(request, purchaseData)) match {
          case login: String =>
            val purchase = new Purchase(purchaseData.product, purchaseData.amount, login, purchaseData.groupID)
            logic.savePurchase(purchase)
            Ok(purchase.toJson)
          case _ =>
            Ok(ErrorMessage.notAuthorised).withNewSession
        }
      }
    )
  }

  def groupInfo(id: Int) = Action {
    logic.getUsersInGroup(id) match {
      case Some(list: List[User]) => Ok(views.html.app.group("Group info", id, list))
      case _ =>
          Ok(views.html.incl.error("Group info", {
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
    logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        var last = -1
        Ok(Json.toJson(logic.getGroupedProductFromGroup(login, 1).map(pur => {
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

  def createGroup = Action(BodyParsers.parse.json) { request =>
    request.body.validate[Application.GroupData].fold(
      error => BadRequest(ErrorMessage.wrongFormat),
      groupData => {
        logic.getLoginBySessionID(Utils.getSessionID(request, groupData)) match {
          case login: String =>
            logic.createGroup(groupData.groupName, login)
            Ok(Json.obj(
              "groupName" -> groupData.groupName,
              "author" -> login
            ))
          case _ =>
            Ok(ErrorMessage.errorWhileHandlingRequest)
        }
      }
    )
  }

  def getCreateGroup = Action { request =>
    logic.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login =>
        Ok(views.html.app.createGroup())
      case _ =>
        Redirect(routes.AuthController.getLoginPage).withNewSession
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

  case class GroupData(groupName: String, description: String,
                       override val sessionID: Option[String] = None) extends IncomeData

  implicit val groupReads: Reads[GroupData] = (
    (JsPath \ "groupName") .read[String] and
      (JsPath \ "description") .read[String] and
      (JsPath \ Utils.session_tag).readNullable[String]
    )(GroupData.apply _)
}