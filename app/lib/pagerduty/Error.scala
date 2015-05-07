package lib.pagerduty

import play.api.libs.json.JsError

sealed class PagerDutyApiError(val msg: String) extends Throwable {
  override def toString = msg
}

object PagerDutyApiError {
  def apply(msg: String) = new PagerDutyApiError(msg)
  // may put some more intelligent processing of the JSON error here
  def apply(err: JsError) = new PagerDutyApiError(err.toString)
}
