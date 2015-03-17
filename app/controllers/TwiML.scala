package controllers

import com.twilio.sdk.verbs._
import play.api.mvc._

/**
  * This file implements endpoints which return TwiML commands to
  * Twilio when it requests them (e.g. in resonse to receiving a call)
  */

object TwiML extends Controller {

  def twTest = Action {
    val res = new TwiMLResponse()
    Ok(res.toXML)
  }
}
