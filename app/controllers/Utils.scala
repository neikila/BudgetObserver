package controllers

import play.api.libs.json.JsValue
import play.api.mvc.{DiscardingCookie, Result, AnyContent, Request}

/**
  * Created by neikila on 29.12.15.
  */
object Utils {
  val session_tag = "session_id"

  def getSessionID(request: Request[JsValue], incomeData: IncomeData): Option[String] = {
    incomeData.sessionID match {
      case session: Some[String] => session
      case _ => request.session.get(session_tag)
    }
  }

  def getSessionID(request: Request[AnyContent]): Option[String] = {
    request.session.get(session_tag)
  }

  def fullDeauth(result: Result): Result = result.discardingCookies(DiscardingCookie("isInit")).withNewSession
}
