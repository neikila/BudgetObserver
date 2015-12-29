package controllers

import play.api.libs.json.Json

/**
  * Created by neikila on 29.12.15.
  */
object JSONResponse {
  val success = Json.obj(
    "result" -> "success"
  )
}
