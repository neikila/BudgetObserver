package models

import anorm.Row
import play.api.libs.json.Json

/**
  * Created by neikila on 24.12.15.
  */

class Purchase (val productName: String, val amount: Int, val login: String, val groupID: Int) {
  override def toString: String = {
    "Product: " + productName + ", Amount: " + amount + " Login: " + login + " GroupID: " + groupID
  }

  def toJson = {
    Json.obj(
      "product" -> productName,
      "amount" -> amount
    )
  }
}

object Purchase {
  def apply(row: Row) = {
    new Purchase(row[String]("product"), row[Int]("amount"), row[String]("login"), row[Int]("groupID"))
  }
}

