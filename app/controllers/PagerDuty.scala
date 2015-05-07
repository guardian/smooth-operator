package controllers

import lib.pagerduty.PagerDutyAPI
import play.api.mvc._
import play.api.Play.{current, configuration}
import play.api.libs.json.{Json, JsArray}
import play.Logger
import lib.OperatorConfig.defaultExecutionContext

object PagerDuty extends Controller {
  val pg = PagerDutyAPI.default
  def whoson = Action.async {
    val schedId = configuration.getString("pagerduty.schedule_id").get
    pg.whoIsOn(schedId) map {
      case Right(users) =>
        Ok(users.mkString(", ") + "\n")
      case Left(err) =>
        InternalServerError(err)
    }
  }

  def contact = Action.async {
    pg.contactNumber("PQU2X2D") map {
      case Right(contact) => Ok(contact)
      case Left(err)      => InternalServerError(err)
    }
  }
}
