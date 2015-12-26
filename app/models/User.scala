package models

import anorm.Row

/**
  * Created by neikila on 25.12.15.
  */
class User(val login: String, val name: String, val surname: String, val email: String)

object User {
  def apply(row: Row): User = {
    new User(row[String]("login"), row[String]("name"), row[String]("surname"), row[String]("email"))
  }
}