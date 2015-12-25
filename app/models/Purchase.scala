package models

import play.api.libs.json.Json

/**
  * Created by neikila on 24.12.15.
  */

class Purchase (val productName: String, val amount: Int, val username: String) {
  override def toString: String = {
    "Product: " + productName + ", Amount: " + amount + " Username: " + username
  }

  def toJson = {
    Json.obj(
      "product" -> productName,
      "amount" -> amount
    )
  }
}

