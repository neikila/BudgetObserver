package models

import java.util.Base64

import scala.collection.immutable
import scala.collection.immutable.HashMap
import scala.util.Random

/**
  * Created by neikila on 25.12.15.
  */
object Logic {
  def getPurchases(username: String) = {
    DBAccess.getPurchases(username)
  }

  def getAllPurchases = {
    DBAccess.getAllPurchases
  }

  def savePurchase(purchase: Purchase) = {
    DBAccess.saveInDB(purchase)
  }

  def getUsersInGroup(groupId: Int) = {
    DBAccess.getUsersInGroup(groupId)
  }

  def getGroup(groupID: Int) = {
    DBAccess.getGroup(groupID)
  }

  def createGroup(groupName: String, author: String) = {
    DBAccess.createGroup(author, groupName)
    DBAccess.includeUserInGroup(author, groupName)
  }

  def auth(login: String, pass: String) = {
    pass == DBAccess.getLoginData(login).password
  }

  var mapSessionIdToMap = new HashMap[String, String]

  def createSessionID(login: String) = {
    val user = DBAccess.getUser(login)
    val pass = DBAccess.getLoginData(login)
    val base = user.login + user.name + pass.password
    val sessionID = shuffleString(Base64.getEncoder.encode(shuffleString(base).getBytes).toString)
    mapSessionIdToMap += (sessionID -> login)
    sessionID
  }

  def shuffleString(string: String) = {
    Random.shuffle(string.toCharArray.toSeq).mkString
  }

  def getLoginBySessionID(sessionID: String) = {
    if (mapSessionIdToMap.contains(sessionID)) mapSessionIdToMap(sessionID) else false
  }

  def getLoginBySessionID(income: Any) = {
    income match {
      case Some(sessionID: String) => if (mapSessionIdToMap.contains(sessionID)) mapSessionIdToMap(sessionID) else false
      case _ => false
    }
  }

}
