package controllers

import lib.pagerduty.{PagerDutyAPI, PagerDutyApiError}
import play.api.mvc._
import play.api.Play.{current, configuration}
import play.api.libs.json.{Json, JsArray}
import play.Logger
import lib.OperatorConfig.defaultExecutionContext

object PagerDuty extends Controller {
  val pg = PagerDutyAPI.default
  def whoson = Action.async {
    val id = configuration.getString("pagerduty.schedule_id").get
    pg.whoIsOn(id)
      .map(users => Ok(users.mkString(", ") + "\n"))
      .recover {
      case err: PagerDutyApiError => InternalServerError(err.toString)
    }
  }
}
