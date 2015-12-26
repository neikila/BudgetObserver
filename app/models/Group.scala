package models

import anorm.Row

/**
  * Created by neikila on 26.12.15.
  */
class Group(val groupName: String, val id: Int)

object Group {
  def apply(row: Row) = {
    new Group(row[String]("groupname"), row[Int]("id"))
  }
}
