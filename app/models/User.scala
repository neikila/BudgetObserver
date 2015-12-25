package models

import anorm.Row

/**
  * Created by neikila on 25.12.15.
  */
class User(val id: Int, val login: String, val name: String, val surname: String, val groupNum: Int)

object User {
  def apply(row: Row): User = {
    new User(row[Int]("id"), row[String]("login"), row[String]("name"), row[String]("surname"), row[Int]("groupid"))
  }
}