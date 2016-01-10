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
  private val wrongFormatCode = 5
  private val wrongAuthCode = 6
  private val suchUserExistCode = 7
  private val passwordsDifferCode = 8
  private val noGroupWithSuchNameCode = 9
  private val suchGroupNameAlreadyExistCode = 10

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

  val wrongFormat = {
    Json.obj(
      "error" -> "Wrong request format",
      "code" -> wrongFormatCode
    )
  }

  def wrongFormat(extraMessage: String) = {
    Json.obj(
      "error" -> "Wrong request format",
      "extraMessage" -> extraMessage,
      "code" -> wrongFormatCode
    )
  }

  val wrongAuth = {
    Json.obj(
      "error" -> "Wrong pass or login",
      "code" -> wrongAuthCode
    )
  }

  val suchUserExist = {
    Json.obj(
      "error" -> "This login isn't available",
      "code" -> suchUserExistCode
    )
  }

  val passwordsDiffer = {
    Json.obj(
      "error" -> "Passwords differ",
      "code" -> passwordsDifferCode
    )
  }

  val noGroupWithSuchName = {
    Json.obj(
      "error" -> "No group with such groupName",
      "code" -> noGroupWithSuchNameCode
    )
  }

  val suchGroupNameAlreadyExist = {
    Json.obj(
      "error" -> "Group with such name already exist",
      "code" -> suchGroupNameAlreadyExistCode
    )
  }
}
