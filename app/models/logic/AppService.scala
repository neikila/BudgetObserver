package models.logic

import java.util.Base64

import controllers.Application.PurchaseData
import controllers.AuthController.SignupData
import models.{DBService, Login, Purchase, User}

import scala.collection.immutable.HashMap
import scala.util.Random

/**
  * Created by neikila on 25.12.15.
  */
class AppService {
  val db = new DBService

  def getPurchases(username: String) = {
    db.getPurchases(username)
  }

  def getGroupPurchases(login: String, groupNameOptional: Option[String] = None): List[Purchase] = {
    val groupName = groupNameOptional match {
      case Some(groupNameVal: String) => groupNameVal
      case _ => getDefaultGroupName(login)
    }
    db.getGroupIdBy(login, groupName) match {
      case Some(id: Int) =>
        db.getPurchasesFromGroup(id)
      case _ =>
        println("No group with groupName: " + groupName + " and login: " + login)
        List[Purchase]()
    }
  }

  def getDefaultGroupName(login: String): String = {
    db.getDefaultUsersGroup(login) match {
      case Some(groupName: String) => groupName
      case _ =>
        println("It will never happen. Method: Logic.getDefaultGroupName")
        "Error"
    }
  }

  def getAllPurchases = {
    db.getAllPurchases
  }

  def savePurchase(login: String, purchaseData: PurchaseData): Purchase = {
    val purchase = db.getGroupIdBy(login, purchaseData.groupName) match {
      case Some(id: Int) =>
        new Purchase(purchaseData.product, purchaseData.amount, login, id)
    }
    db.saveInDB(purchase)
    purchase
  }

  def getUsersInGroup(groupId: Int): Option[List[User]] = {
    val list = db.getUsersInGroup(groupId)
    if (list isEmpty) {
      None
    } else {
      Some(list)
    }
  }

  def getGroup(groupID: Int) = {
    db.getGroup(groupID)
  }

  def createGroup(groupName: String, author: String) = {
    db.createGroup(author) match {
      case Some(id: Long) =>
        db.includeUserInGroup(id, author, groupName)
      case _ =>
        println("This will never happen. I Hope...")
    }
  }

  def getGroupedProductFromGroup(login: String, groupName: String) = {
    db.getGroupIdBy(login, groupName) match {
      case Some(id: Int) =>
        db.getGroupedProductFromGroup(login, id)
      case _ =>
        println("Something Went Wrong")
        List[Purchase]()
    }
  }
}

object AppService {
  val logic = new AppService
  val defaultGroupName = "First_budget"

  def apply() = {
    logic
  }
}
