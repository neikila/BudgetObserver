package models

import anorm.Row

/**
  * Created by neikila on 26.12.15.
  */
class Login(val login: String, val password: String)

object Login {
  def apply(row: Row) = {
    new Login(row[String]("login"), row[String]("password"))
  }
}
