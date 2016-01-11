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

  def getPurchasesFromGroup(groupID: Int) = {
    DB.withConnection { implicit c =>
      val sql = SQL("select * from purchase where groupID={groupID};").on("groupID" -> groupID)
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

  def getDefaultUsersGroup(login: String): Option[String] = {
    DB.withConnection { implicit c =>
      val sql = SQL("select groupName from usersToGroup where isDefault='true' and login={login} limit 1;").on("login" -> login)
      sql().headOption match {
        case Some(row: Row) => Some(row[String]("groupName"))
        case _ => None
      }
    }
  }

  def createGroup(author: String, description: String): Option[Long] = {
    DB.withConnection { implicit c =>
      SQL("insert into groups (description, author) values({description}, {author});")
        .on(
          "description" -> description,
          "author" -> author
        ).executeInsert()
    }
  }

  def createGroup(author: String): Option[Long] = {
    DB.withConnection { implicit c =>
      SQL("insert into groups (author) values({author});")
        .on(
          "author" -> author
        ).executeInsert()
    }
  }

  def getGroupIdBy(login: String, groupName: String): Option[Int] = {
    DB.withConnection { implicit c =>
      val sql = SQL("select groupID from usersToGroup where login = {author} and groupName = {groupName};")
        .on(
          "groupName" -> groupName,
          "author" -> login
        )
      sql().headOption match {
        case Some(row: Row) => Some(row[Int]("groupID"))
        case _ => None
      }
    }
  }

  def includeUserInGroup(groupID: Long, login: String, groupName: String, isDefault: Boolean = false) = {
    DB.withConnection { implicit c =>
      SQL(
        "insert into usersToGroup(groupID, login, groupName, isDefault) " +
          "values({groupID}, {login}, {groupName}, {isDefault});"
      ).on(
        "groupID" -> groupID,
        "groupName" -> groupName,
        "login" -> login,
        "isDefault" -> {if (isDefault) "true" else "false"}
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

  def getAllUserGroups(login: String): List[String] = {
    DB.withConnection { implicit c =>
      SQL("select groupName from usersToGroup where login = {login};")
        .on("login" -> login)().map(row => row[String]("groupName")).toList
    }
  }
}
