package controllers

import lib.PagerDutyAPI
import play.api.mvc._
import play.api.Play.{current, configuration}
import play.api.libs.json.{Json, JsArray}
import play.Logger
import lib.OperatorConfig.defaultExecutionContext

object PagerDuty extends Controller {
  val pg = PagerDutyAPI.default
  def whoson = Action.async {
    val id = configuration.getString("pagerduty.schedule_id").get
    pg.whoIsOn(id) map { response =>
      val json = Json.parse(response.body)
      json \ "users" match {
        case usersArray: JsArray =>
          val users = usersArray.value.map(_ \ "email").mkString(", ")
          Ok(users + "\n")
        case _ => InternalServerError
      }
    }
  }
}
