package models

import anorm._
import play.api.db.DB
import play.api.Play.current

/**
  * Created by neikila on 25.12.15.
  */
object DBAccess {
  def saveInDB(purchase: Purchase) = {
    DB.withConnection { implicit c =>
      SQL("insert into purchase(product, amount, login, groupID) " +
        "values({productName}, {productAmount}, {login}, {groupID});")
        .on(
          "productName" -> purchase.productName,
          "productAmount" -> purchase.amount,
          "login" -> purchase.login,
          "groupID" -> purchase.groupID
        ).executeUpdate()
    }
  }

  def getAllPurchases = {
    DB.withConnection { implicit c =>
      val sql = SQL("select * from purchase;")
      sql().map(row => Purchase(row)).toList
    }
  }

  def getPurchases(login: String) = {
    DB.withConnection { implicit c =>
      val sql = SQL("select * from purchase;").on("login" -> login)
      sql().map(row => Purchase(row)).toList
    }
  }

  def getUsersInGroup(groupID: Int) = {
    DB.withConnection { implicit c =>
      val sql = SQL("select users.* from users join usersToGroup as UTG " +
        "where users.login = UTG.login and groupID = {groupID};")
        .on("groupID" -> groupID)
      sql().map(row => User(row)).toList
    }
  }

  def getUser(login: String) = {
    DB.withConnection { implicit c =>
      val sql = SQL("select * from users where login={login};").on("login" -> login)
      User(sql().head)
    }
  }

  def getGroup(id: Int) = {
    DB.withConnection { implicit c =>
      val sql = SQL("select * from groups where id={groupID};").on("groupID" -> id)
      Group(sql().head)
    }
  }

  def createGroup(author: String, groupName: String) = {
    DB.withConnection { implicit c =>
      SQL("insert into groups (groupname, author) values({groupName}, {author});")
        .on(
          "groupName" -> groupName,
          "author" -> author
        ).executeUpdate
    }
  }

  def includeUserInGroup(login: String, groupName: String) = {
    DB.withConnection { implicit c =>
      SQL(
        "insert into usersToGroup(groupID, login) " +
          "values((select id from groups where groupname={groupName} and author={author}),{author});"
      ).on(
        "groupName" -> groupName,
        "author" -> login
      ).executeUpdate()
    }
  }

  def getLoginData(login: String) = {
    DB.withConnection { implicit c =>
      val sql = SQL("select * from login where login={login};").on("login" -> login)
      Login(sql().head)
    }
  }
}
