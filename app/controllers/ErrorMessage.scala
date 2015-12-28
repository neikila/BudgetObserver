package controllers

import play.api.libs.json.Json

/**
  * Created by neikila on 28.12.15.
  */
object ErrorMessage {
  val notAuthorisedCode = 1
  val alreadyAuthorisedCode = 2

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
}
