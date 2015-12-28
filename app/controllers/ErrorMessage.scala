package controllers

import play.api.libs.json.Json

/**
  * Created by neikila on 28.12.15.
  */
object ErrorMessage {
  private val notAuthorisedCode = 1
  private val alreadyAuthorisedCode = 2
  private val errorWhileHandlingRequestCode = 3

  def notAuthorised = {
    Json.obj(
      "error" -> "Not authorised",
      "code" -> notAuthorisedCode
    )
  }

  def alreadyAuthorised = {
    Json.obj(
      "error" -> "Already authorised",
      "code" -> alreadyAuthorisedCode
    )
  }

  def errorWhileHandlingRequest = {
    Json.obj(
      "error" -> "Server error",
      "code" -> errorWhileHandlingRequestCode
    )
  }
}
