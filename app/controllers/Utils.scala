package controllers

import models.Logic
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Request}

/**
  * Created by neikila on 29.12.15.
  */
object Utils {
  def getSessionID(request: Request[JsValue], incomeData: IncomeData): Option[String] = {
    incomeData.sessionID match {
      case session: Some[String] => session
      case _ => request.session.get("session_id")
    }
  }

  def getSessionID(request: Request[AnyContent]): Option[String] = {
    request.session.get("session_id")
  }
}
