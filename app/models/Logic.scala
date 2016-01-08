package models

import java.util.Base64

import controllers.AuthController.SignupData

import scala.collection.immutable.HashMap
import scala.util.Random

/**
  * Created by neikila on 25.12.15.
  */
class Logic {
  var mapSessionToLogin = new HashMap[String, String]
  var mapLoginToSession = new HashMap[String, String]
  val db = new DBService

  def getPurchases(username: String) = {
    db.getPurchases(username)
  }

  def getAllPurchases = {
    db.getAllPurchases
  }

  def savePurchase(purchase: Purchase) = {
    db.saveInDB(purchase)
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

  def auth(login: String, pass: String): Option[String] = {
    db.getLoginData(login) match {
      case Some(loginData: Login) =>
        if (loginData.password == pass) {
          Some(this.login(loginData))
        } else {
          None
        }
      case _ => None
    }
  }

  def login(loginData: Login): String = {
    mapLoginToSession.get(loginData.login) match {
      case Some(session) =>
        var newSession = createSessionID(loginData)
        while (newSession == session) {
          newSession = createSessionID(loginData)
        }
        newSession
      case _ =>
        val session = createSessionID(loginData)
        addSession(session, loginData.login)
        session
    }
  }

  def createSessionID(loginData: Login): String = {
    val base = shuffleString(loginData.login).substring(0, Logic.minLoginSize) +
      shuffleString(loginData.password).substring(0, Logic.minPassSize)
    Base64.getEncoder.encode((
      loginData.login + shuffleString(Base64.getEncoder.encode(shuffleString(base).getBytes).toString))
      .getBytes)
      .toString
  }

  def addSession(session: String, login: String): Unit = {
    mapSessionToLogin += (session -> login)
    mapLoginToSession += (login -> session)
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

  def createUser(signupData: SignupData): Option[String] = {
    if (signupData.password == signupData.password_repeat) {
      db.getUser(signupData.login) match {
        case Some(user: User) =>
          Some("UserExist")
        case _ =>
          db.saveUser(new User(signupData.login, signupData.name, signupData.surname, signupData.email))
          createGroup(Logic.defaultGroupName, signupData.login)
          val loginData = new Login(signupData.login, signupData.password)
          db.saveLogin(loginData)
          Some(login(loginData))
      }
    } else {
      Some("PasswordsDiffer")
    }
  }

  def getGroupedProductFromGroup(login: String, groupID: Int) = {
    db.getGroupedProductFromGroup(login, groupID)
    //    array.foreach((a: String) => println(a))
    //    DBAccess.getAllPurchases.groupBy(pur => pur.productName)
    //      .foreach((para: (String, List[Purchase])) => {
    //        val (product, list) = para
    //        list.sum()
    //      })
  }
}

object Logic {
  val logic = new Logic
  val minLoginSize = 4
  val minPassSize = 4
  val defaultGroupName = "First budget"

  def apply() = {
    logic
  }
}
