package lib

import java.net.URI
import play.api.libs.ws.WS
import play.api.Play.{current, configuration}

class PagerDutyAPI(authToken: String, base: String, apiVersion: String = "v1") {
  require(base.startsWith("https"), "Please don't use Pager Duty without HTTPS")

  private def authHeader = ("Authorization" -> s"Token token=$authToken")

  private def request(path: String) = WS.url(s"$base/api/$apiVersion/$path").withHeaders(authHeader)

  private def submit(path: String) = request(path).execute

  /**
    *  https://developer.pagerduty.com/documentation/rest/schedules/users
    */
  def whoIsOn(scheduleId: String) = submit(s"schedules/$scheduleId/users")
}

object PagerDutyAPI {
  lazy val default = new PagerDutyAPI(configuration.getString("pagerduty.token").get,
                                      configuration.getString("pagerduty.url").get)
}
