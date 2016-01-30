package models


import java.util.Date

import anorm.Row

/**
  * Created by neikila on 26.12.15.
  */
class Group(val id: Int, val groupName: String, val author: String, val description: String, val dateOfCreation: Date)

object Group {
  def apply(row: Row) = {
    new Group(row[Int]("id"), row[String]("groupName"), row[String]("author"), row[String]("description"), row[Date]("dateOfCreation"))
  }
}
