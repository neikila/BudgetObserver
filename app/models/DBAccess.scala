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
      val result = SQL("insert into purchase(product, amount, login, groupID) " +
        "values({productName}, {productAmount}, {login}, {groupID});")
        .on(
          "productName" -> purchase.productName,
          "productAmount" -> purchase.amount,
          "login" -> purchase.login,
          "groupID" -> purchase.groupID
        ).executeUpdate()
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
}
