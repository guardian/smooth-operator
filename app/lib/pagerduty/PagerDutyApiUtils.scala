package lib.pagerduty

import play.api.libs.json._
import play.api.libs.ws.{WSResponse, WS}
import scala.concurrent.{ExecutionContext, Future}
import play.api.Play.current
import play.api.http.Status

trait PagerDutyApiUtils extends Status {

  require(baseUrl.startsWith("https"), "Please don't use Pager Duty without HTTPS")

  val authToken: String
  val baseUrl: String
  val apiVersion: String = "v1"

  protected def authHeader = ("Authorization" -> s"Token token=$authToken")

  protected def request(path: String) = WS.url(s"$baseUrl/api/$apiVersion/$path").withHeaders(authHeader)

  protected def submit(path: String, params: (String, String)*)(implicit ec: ExecutionContext):
      Future[WSResponse] = request(path).withQueryString(params: _*).execute

  protected def submitJson(path: String, params: (String, String)*)
                          (implicit ec: ExecutionContext): Future[JsValue] =
    submit(path, params: _*).map { response =>
      if(response.status == OK) response.json else throw getError(response.json)
    }

  /**
    *  checks whether the WSResponse that was returned by PagerDuty was
    *  an error (see:
    *  https://developer.pagerduty.com/documentation/rest/errors
    */
  private def getError(err: JsValue): PagerDutyApiError = {
    val mainMsg = (err \ "error" \ "message").as[String]
    val otherMsgs = (err \ "error" \ "errors") match {
      case JsArray(errs)  => "; " + errs.map(_.toString).mkString("; ")
      case _ => ""
    }
    PagerDutyApiError(s"PagerDuty Error: ${mainMsg}${otherMsgs}")
  }

}
