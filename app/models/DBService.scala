package models

import anorm._
import play.api.db.DB
import play.api.Play.current

/**
  * Created by neikila on 25.12.15.
  */
class DBService {
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
      val sql = SQL("select * from purchase where login={login};").on("login" -> login)
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

  def getUser(login: String): Option[User] = {
    DB.withConnection { implicit c =>
      val sql = SQL("select * from users where login={login};").on("login" -> login)
      sql().headOption match {
        case Some(row: Row) => Some(User(row))
        case _ => None
      }
    }
  }

  def getGroup(id: Int): Option[Group] = {
    DB.withConnection { implicit c =>
      val sql = SQL("select * from groups where id={groupID};").on("groupID" -> id)
      sql().headOption match {
        case Some(row: Row) => Some(Group(row))
        case _ => None
      }
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

  def getLoginData(login: String): Option[Login] = {
    DB.withConnection { implicit c =>
      println("login: " + login)
      val sql = SQL("select * from login where login={login};").on("login" -> login)
      sql().headOption match {
        case Some(row: Row) => Some(Login(row))
        case _ => None
      }
    }
  }

  def saveUser(user: User) = {
    DB.withConnection { implicit c =>
      SQL("insert into users(login, name, surname, email) " +
        "values({login}, {name}, {surname}, {email});")
        .on(
          "login" -> user.login,
          "name" -> user.name,
          "surname" -> user.surname,
          "email" -> user.email
        ).executeUpdate
    }
  }

  def saveLogin(login: Login) = {
    DB.withConnection { implicit c =>
      SQL("insert into login(login, password) " +
        "values({login}, {password});")
        .on(
          "login" -> login.login,
          "password" -> login.password
        ).executeUpdate
    }
  }

  def getGroupedProductFromGroup(login: String, groupID: Int): List[Purchase] = {
    DB.withConnection { implicit c =>
      SQL("select product, login, groupID, SUM(amount) as amount from purchase " +
        "where groupID = {groupID} and login = {login}" +
        "group by product order by product;")
        .on(
          "groupID" -> groupID,
          "login" -> login
        )().map(row =>
          new Purchase(row[String]("product"), row[java.math.BigDecimal]("amount").intValue(), row[String]("login"), row[Int]("groupID"))
      ).toList
    }
  }
}
