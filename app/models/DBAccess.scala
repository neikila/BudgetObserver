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
      val result = SQL("insert into purchase (product, amount, username) " +
        "values({productName}, {productAmount}, {username});")
        .on("productName" -> purchase.productName)
        .on("productAmount" -> purchase.amount)
        .on("username" -> purchase.username)
        .executeUpdate()
    }
  }

  def getPurchases(username: String) = {
    DB.withConnection { implicit c =>
      val sql = SQL("select * from purchase;").on("username" -> username)
      val purchases = sql().map(row =>
        new Purchase(row[String]("product"), row[Int]("amount"), username)
      ).toList
      purchases
    }
  }
}
