package controllers

import play.api.libs.json.Json

/**
  * Created by neikila on 28.12.15.
  */
object ErrorMessage {
  private val notAuthorisedCode = 1
  private val alreadyAuthorisedCode = 2
  private val errorWhileHandlingRequestCode = 3
  private val notGroupMemberCode = 4

  val notAuthorised = {
    Json.obj(
      "error" -> "Not authorised",
      "code" -> notAuthorisedCode
    )
  }

  val alreadyAuthorised = {
    Json.obj(
      "error" -> "Already authorised",
      "code" -> alreadyAuthorisedCode
    )
  }

  val errorWhileHandlingRequest = {
    Json.obj(
      "error" -> "Server error",
      "code" -> errorWhileHandlingRequestCode
    )
  }

  val notGroupMember = {
    Json.obj(
      "error" -> "Not a group member",
      "code" -> notGroupMemberCode
    )
  }
}
