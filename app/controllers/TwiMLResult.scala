package controllers

import play.api.mvc.{Result, Controller}
import com.twilio.sdk.verbs._

trait TwiMLController extends Controller {
  implicit def twimlToResult(twiml: TwiMLResponse): Result = {
    Ok(twiml.toXML).withHeaders("Content-Type" -> "text/xml")
  }
}
