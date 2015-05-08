package controllers

import lib.pagerduty.{PagerDutyAPI, PagerDutyApiError, User}
import scala.concurrent.Future
import play.api.mvc._
import play.api.Play.{current, configuration}
import play.api.libs.json.{Json, JsArray}
import play.Logger
import lib.OperatorConfig.defaultExecutionContext

object PagerDutyController extends Controller {
  val pg = PagerDutyAPI.default
  val schedId = configuration.getString("pagerduty.schedule_id").get

  object PagerDutyApiAction extends ActionBuilder[Request] {
    // detects PagerDuty errors and returns internal server error
    def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      block(request) recover {
        case err: PagerDutyApiError => InternalServerError(err.toString)
      }
    }
  }

  def whoson = PagerDutyApiAction.async {
    pg.whoIsOn(schedId).map(users => Ok(users.mkString(", ") + "\n"))
  }

  def contact = PagerDutyApiAction.async {
    pg.whoYaGonnaCall(schedId) map { users =>
      // get all of the numbers that are present
      val withNums = users.collect {
        case User(_, _, Some(num)) => num
      }
      if(withNums.length > 0)
        Ok(withNums.mkString(","))
      else
        InternalServerError("no phone numbers found")
    }
  }
}
