package lib.pagerduty

import java.net.URI
import play.api.Play.{current, configuration}
import play.api.libs.json._
import play.api.http.Status
import org.joda.time.DateTime
import scala.concurrent.{ExecutionContext, Future}

import JsonImplicits._

class PagerDutyAPI(val authToken: String, val baseUrl: String) extends PagerDutyApiUtils {
/**
  *  https://developer.pagerduty.com/documentation/rest/schedules/users
  */
def whoIsOn(scheduleId: String, startDate: DateTime, endDate: DateTime)(implicit ec: ExecutionContext):
    Future[Either[String, Seq[User]]] = {
  val future =
    submitJson(s"schedules/$scheduleId/users",
               "since" -> startDate.toString,
               "until" -> endDate.toString)
  future map { response =>
    response.right.flatMap { json =>
      (json \ "users").validate[Seq[User]] match {
        case JsSuccess(users, _) => Right(users)
        case err: JsError     => Left(err.toString)
      }
    }
  }
}

  /**
    * default to today
    */
  def whoIsOn(scheduleId: String)(implicit ec: ExecutionContext): Future[Either[String, Seq[User]]] =
    whoIsOn(scheduleId, new DateTime, new DateTime)
}

object PagerDutyAPI {
  lazy val default = new PagerDutyAPI(configuration.getString("pagerduty.token").get,
                                      configuration.getString("pagerduty.url").get)
}
