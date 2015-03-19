package lib

import java.net.URI
import play.api.libs.ws.WS
import play.api.Play.{current, configuration}

class PagerDutyAPI(authToken: String, base: String, apiVersion: String = "v1") {
  require(base.startsWith("https"), "Please don't use Pager Duty without HTTPS")

  def authHeader = ("Authorization" -> "Token token=$authToken")

  def request(path: String) = WS.url(s"$base/api/$apiVersion/$path").withHeaders(authHeader)

  def submit(path: String) = request(path).execute

}

object PagerDutyAPI {
  lazy val default = new PagerDutyAPI(configuration.getString("pagerduty.token").get,
                                      configuration.getString("pagerduty.url").get)
}
