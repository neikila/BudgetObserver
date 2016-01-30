package controllers

import models.logic.{AppService, AuthService}
import models.{Group, User, Purchase}
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

class Application extends Controller {
  val logic = AppService()
  val authService = AuthService()

  def purchasesWithGroupName(groupNameRequest: String) = purchases(Some(groupNameRequest))

  def purchasesDefault = purchases(None)

  def purchases(groupNameRequest: Option[String]) = Action { request =>
    authService.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        Ok(views.html.app.purchases())
      case _ =>
        Utils.fullDeauth(Redirect(routes.AuthController.getLoginPage))
    }
  }

  def getPurchasesJSONDefault = getPurchasesJSON(None)
  def getPurchasesJSONWithGroupName(groupName: String) = getPurchasesJSON(Some(groupName))

  implicit val purchaseWrites: Writes[Purchase] = (
    (__ \ "login").write[String] and
      (__ \ "productName").write[String] and
      (__ \ "amount").write[Int]
    )(unlift((purchase: Purchase) => Some(purchase.login, purchase.productName, purchase.amount)))

  def getPurchasesJSON(groupNameRequest: Option[String]) = Action { request =>
    authService.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        logic.getGroupPurchases(login, groupNameRequest) match {
          case (groupName, Some(list)) =>
            Ok(Json.obj(
              "groupName" -> groupName,
              "purchases" -> list
            ))
          case (groupName, _) =>
            Ok(ErrorMessage.noGroupWithSuchName)
        }
      case _ =>
        Ok(ErrorMessage.notAuthorised)
    }
  }

  def index = Action {
    Ok(views.html.app.index())
  }

  def documentation= Action {
    Ok(views.html.app.doc("Your new application is ready."))
  }

  def savePurchaseJSON = Action(BodyParsers.parse.json) { implicit request =>
    request.body.validate[Application.PurchaseData].fold(
      error => BadRequest(ErrorMessage.notAuthorised),
      purchaseData => {
        authService.getLoginBySessionID(Utils.getSessionID(request, purchaseData)) match {
          case login: String =>
            logic.savePurchase(login, purchaseData) match {
              case Some(purchase: Purchase) =>
                Ok(purchase.toJson)
              case _ =>
                Ok(ErrorMessage.noGroupWithSuchName)
            }
          case _ =>
            Utils.fullDeauth(Ok(ErrorMessage.notAuthorised))
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

  def getGroupPieData(groupName: String) = Action { request =>
    authService.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        var last = -1
        Ok(Json.toJson(logic.getGroupedProductFromGroup(login, groupName).map(pur => {
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
        authService.getLoginBySessionID(Utils.getSessionID(request, groupData)) match {
          case login: String =>
            logic.createGroup(groupData.groupName, groupData.description, login) match {
              case true =>
                Ok(Json.obj(
                  "groupName" -> groupData.groupName,
                  "author" -> login
                ))
              case _ =>
                Ok(ErrorMessage.suchGroupNameAlreadyExist)
            }
          case _ =>
            Ok(ErrorMessage.errorWhileHandlingRequest)
        }
      }
    )
  }

  def getCreateGroup = Action { request =>
    authService.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        Ok(views.html.app.createGroup())
      case _ =>
        Utils.fullDeauth(Redirect(routes.AuthController.getLoginPage))
    }
  }

  def getGroupManaging = Action { request =>
    authService.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String =>
        Ok(views.html.app.groupManaging())
      case _ =>
        Utils.fullDeauth(Redirect(routes.AuthController.getLoginPage))
    }
  }

  def getAllGroups = Action { request =>
    authService.getLoginBySessionID(Utils.getSessionID(request)) match {
      case login: String => Ok(Json.toJson(logic.getUsersGroups(login)))
      case _ => Ok(ErrorMessage.notAuthorised)
    }
  }

  implicit val groupWrites: Writes[Group] = (
    (__ \ "groupName").write[String] and
      (__ \ "author").write[String] and
      (__ \ "description").write[String] and
      (__ \ "dateOfCreating").write[String]
    )(unlift((group: Group) => Some(group.groupName, group.author, group.description, group.dateOfCreation.toString)))
}

object Application {

  case class PurchaseData(product: String, amount: Int, groupName: String, override val sessionID: Option[String] = None) extends IncomeData

  implicit val purchaseReads: Reads[PurchaseData] = (
    (JsPath \ "product") .read[String] and
      (JsPath \ "amount").read[Int] and
      (JsPath \ "groupName").read[String] and
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