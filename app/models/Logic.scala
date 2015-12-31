package models

import java.util.Base64

import controllers.AuthController.SignupData

import scala.collection.immutable.HashMap
import scala.util.Random

/**
  * Created by neikila on 25.12.15.
  */
object Logic {
  var mapSessionToLogin = new HashMap[String, String]
  var mapLoginToSession = new HashMap[String, String]

  def getPurchases(username: String) = {
    DBAccess.getPurchases(username)
  }

  def getAllPurchases = {
    DBAccess.getAllPurchases
  }

  def savePurchase(purchase: Purchase) = {
    DBAccess.saveInDB(purchase)
  }

  def getUsersInGroup(groupId: Int): Option[Any] = {
    val list = DBAccess.getUsersInGroup(groupId)
    if (list isEmpty) {
      Some(None)
    } else {
      Some(list)
    }
  }

  def getGroup(groupID: Int) = {
    DBAccess.getGroup(groupID)
  }

  def createGroup(groupName: String, author: String) = {
    DBAccess.createGroup(author, groupName)
    DBAccess.includeUserInGroup(author, groupName)
  }

  def auth(login: String, pass: String): Boolean = {
    DBAccess.getLoginData(login) match {
      case Some(loginData: Login) => loginData.password == pass
      case _ => false
    }
  }

  def login(login: String): Option[String] = {
    createSessionID(login) match {
      case Some(session: String) =>
        if (addSession(session, login))
          Some(session)
        else
          None
      case _ =>
        None
    }
  }

  def createSessionID(login: String): Option[String] = {
    DBAccess.getUser(login) match {
      case Some(user: User) =>
        DBAccess.getLoginData(login) match {
          case Some(pass: Login) =>
            val base = user.login + user.name + pass.password
            Some(shuffleString(Base64.getEncoder.encode(shuffleString(base).getBytes).toString))
          case _ =>
            None
        }
      case _ =>
        None
    }
  }

  def addSession(session: String, login: String) = {
    if (mapSessionToLogin.contains(session) || mapLoginToSession.contains(login))
      false
    else {
      mapSessionToLogin += (session -> login)
      mapLoginToSession += (login -> session)
      println("Map size: " + mapSessionToLogin.size)
      true
    }
  }

  def logout(login: String) = {
    if (mapLoginToSession.contains(login)) {
      val session = mapLoginToSession(login)
      mapLoginToSession -= login
      if (mapSessionToLogin.contains(session)) mapSessionToLogin -= session
      println("Map size: " + mapSessionToLogin.size)
    }
  }

  def shuffleString(string: String) = {
    Random.shuffle(string.toCharArray.toSeq).mkString
  }

  def getLoginBySessionID(sessionID: String) = {
    if (mapSessionToLogin.contains(sessionID)) mapSessionToLogin(sessionID) else false
  }

  def getLoginBySessionID(income: Option[String]) = {
    income match {
      case Some(sessionID: String) => if (mapSessionToLogin.contains(sessionID)) mapSessionToLogin(sessionID) else false
      case _ => false
    }
  }

  def createUser(signupData: SignupData): Option[Any] = {
    DBAccess.getUser(signupData.login) match {
      case Some(user: User) =>
        Some(false)
      case _ =>
        DBAccess.saveUser(new User(signupData.login, signupData.name, signupData.surname, signupData.email))
        DBAccess.saveLogin(new Login(signupData.login, signupData.password))
        login(signupData.login)
    }
  }

  def getGroupedProductFromGroup(login: String, groupID: Int) = {
    DBAccess.getGroupedProductFromGroup(login, groupID)
    //    array.foreach((a: String) => println(a))
    //    DBAccess.getAllPurchases.groupBy(pur => pur.productName)
    //      .foreach((para: (String, List[Purchase])) => {
    //        val (product, list) = para
    //        list.sum()
    //      })
  }
}
