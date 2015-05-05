package lib.pagerduty

import java.net.URI
import play.api.libs.ws.{WSResponse, WS}
import play.api.libs.json._
import play.api.Play.{current, configuration}
import play.api.http.Status
import org.joda.time.DateTime
import scala.concurrent.{ExecutionContext, Future}

import JsonImplicits._

class PagerDutyAPI(authToken: String, base: String, apiVersion: String = "v1") extends Status {
  require(base.startsWith("https"), "Please don't use Pager Duty without HTTPS")

  private def authHeader = ("Authorization" -> s"Token token=$authToken")

  private def request(path: String) = WS.url(s"$base/api/$apiVersion/$path").withHeaders(authHeader)

  private def submit(path: String, params: (String, String)*)(implicit ec: ExecutionContext) =
    request(path).withQueryString(params: _*).execute

  private def submitJson(path: String, params: (String, String)*)(implicit ec: ExecutionContext):
      Future[Either[String, JsValue]] =
    submit(path, params: _*).map { response =>
      if(response.status == OK) Right(response.json) else Left(getError(response.json))
    }

  /**
    *  checks whether the WSResponse that was returned by PagerDuty was
    *  an error (see:
    *  https://developer.pagerduty.com/documentation/rest/errors
    */
  private def getError(err: JsValue): String = {
    val mainMsg = (err \ "error" \ "message").as[String]
    val otherMsgs = (err \ "error" \ "errors").toString
    s"PagerDuty Error: ${mainMsg}; ${otherMsgs}"
  }

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
