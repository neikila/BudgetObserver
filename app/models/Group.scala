package models

import anorm.Row

/**
  * Created by neikila on 26.12.15.
  */
class Group(val id: Int, val groupName: String, val author: String)

object Group {
  def apply(row: Row) = {
    new Group(row[Int]("id"), row[String]("groupname"), row[String]("author"))
  }
}
