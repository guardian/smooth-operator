package controllers

import lib.PagerDutyAPI.{default => pg}
import play.api.mvc._
import play.api.Play.{current, configuration}
import play.Logger
import lib.OperatorConfig.defaultExecutionContext

object PagerDuty extends Controller {
  def whoson = Action.async {
    pg.submit("/schedules").map(response => Ok(s"${response.status} ${response.body}"))
  }
}
