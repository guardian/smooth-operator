package controllers

import com.twilio.sdk.verbs._
import play.api.mvc._
import play.api.Play.{current, configuration}
import play.Logger

/**
  * This file implements endpoints which return TwiML commands to
  * Twilio when it requests them (e.g. in resonse to receiving a call)
  */

object TwiML extends Controller with TwiMLController {
  def twForwardCall = Action {
    val action = configuration.getString("forwardTo").map { num =>
      Logger.info(s"Receiving call forward request, forwarding to $num")
      new Dial(num)
    } getOrElse {
      Logger.error("Received call forward request but no number configured")
      new Say("No onward destination configured")
    }

    val res = new TwiMLResponse
    res.append(action)
    res
  }
}
