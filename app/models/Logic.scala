package models

import java.util.Base64

import controllers.Application.SignupData

import scala.collection.immutable
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

  def login(login: String): Option[Any] = {
    val session = createSessionID(login)
    if (addSession(session, login))
      Some(session)
    else
      Some(None)
  }

  def createSessionID(login: String) = {
    val user = DBAccess.getUser(login)
    val pass = DBAccess.getLoginData(login)
    val base = user.login + user.name + pass.password
    shuffleString(Base64.getEncoder.encode(shuffleString(base).getBytes).toString)
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

  def getLoginBySessionID(income: Any) = {
    income match {
      case Some(sessionID: String) => if (mapSessionToLogin.contains(sessionID)) mapSessionToLogin(sessionID) else false
      case _ => false
    }
  }

  def createUser(signupData: SignupData) = {
    DBAccess.saveUser(new User(signupData.login, signupData.name, signupData.surname, signupData.email))
    DBAccess.saveLogin(new Login(signupData.login, signupData.password))
    login(signupData.login)
  }
}
