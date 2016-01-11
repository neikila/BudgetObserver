package models.logic

import controllers.Application.PurchaseData
import models.{DBService, Purchase, User}

/**
  * Created by neikila on 25.12.15.
  */
class AppService {
  val db = new DBService

  def getPurchases(username: String) = {
    db.getPurchases(username)
  }

  def getGroupPurchases(login: String, groupNameRequest: Option[String]): (String, Option[List[Purchase]]) = {
    val groupName = groupNameRequest match {
      case Some(value: String) => value
      case _ => getDefaultGroupName(login)
    }
    db.getGroupIdBy(login, groupName) match {
      case Some(id: Int) =>
        (groupName, Some(db.getPurchasesFromGroup(id)))
      case _ =>
        println("No group with groupName: " + groupName + " and login: " + login)
        (groupName, None)
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

  def savePurchase(login: String, purchaseData: PurchaseData): Option[Purchase] = {
    db.getGroupIdBy(login, purchaseData.groupName) match {
      case Some(id: Int) =>
        val purchase = new Purchase(purchaseData.product, purchaseData.amount, login, id)
        db.saveInDB(purchase)
        Some(purchase)
      case _ =>
        None
    }
  }

  def getUsersInGroup(groupId: Int): Option[List[User]] = {
    val list = db.getUsersInGroup(groupId)
    if (list.isEmpty) {
      None
    } else {
      Some(list)
    }
  }

  def getGroup(groupID: Int) = {
    db.getGroup(groupID)
  }

  def createDefaultGroup(groupName: String, author: String): Boolean = {
    db.createGroup(author) match {
      case Some(id: Long) =>
        db.includeUserInGroup(id, author, groupName, isDefault = true)
        true
      case _ =>
        println("This will never happen. I Hope...")
        false
    }
  }

  def createGroup(groupName: String, description: String, author: String): Boolean = {
    db.getGroupIdBy(author, groupName) match {
      case Some(id: Int) =>
        false
      case _ =>
        db.createGroup(author, description) match {
          case Some(id: Long) =>
            db.includeUserInGroup(id, author, groupName)
            true
          case _ =>
            println("This will never happen. Only if some error in db.createGroup")
            false
        }
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

  def getUser(login: String): (Option[User], String, List[String]) = {
    val defGroup = getDefaultGroupName(login)
    val otherGroups = db.getAllUserGroups(login).filter((groupName: String) => groupName != defGroup)
    (db.getUser(login), defGroup, otherGroups)
  }
}

object AppService {
  val logic = new AppService
  val defaultGroupName = "First_budget"

  def apply() = {
    logic
  }
}
