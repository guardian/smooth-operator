package lib.pagerduty

import java.net.URI
import play.api.Play.{current, configuration}
import play.api.libs.json._
import play.api.http.Status
import org.joda.time.DateTime
import scala.concurrent.{ExecutionContext, Future}

import JsonImplicits._

class PagerDutyAPI(val authToken: String, val baseUrl: String) extends PagerDutyApiUtils {

  // finds out who is on-call now for a given schedule, and returns a
  // list of users with a contact number
  def whoYaGonnaCall(scheduleId: String)(implicit ec: ExecutionContext):
      Future[Either[String, Seq[UserWithNumber]]] = {
    val usersFuture: Future[Either[String, Seq[User]]] = whoIsOn(scheduleId)
    usersFuture flatMap { result =>
      // now we have an either which represents the (possibly failing)
      // result from getting the user from the schedule. If it was
      // successful we want to take that list of users and lookup the
      // contact number for each one

      val numberFutures = result.right map { userList => userList.map(contactNumber(_.id)) }

      val numresult = result.right map
      { users => Future.traverse(users)(user => contactNumber(user.id)) }
    }
  }

  /**
    *  https://developer.pagerduty.com/documentation/rest/schedules/users
    */
  def whoIsOn(scheduleId: String, startDate: DateTime, endDate: DateTime)(implicit ec: ExecutionContext):
      Future[Either[String, Seq[User]]] =
    submitJson(s"schedules/$scheduleId/users",
               "since" -> startDate.toString,
               "until" -> endDate.toString) map { response =>
      // parse the JSON into a sequence of Users
      response.right.flatMap { json =>
        (json \ "users").validate[Seq[User]] match {
          case JsSuccess(users, _) => Right(users)
          case err: JsError        => Left(err.toString)
        }
      }
    }

  /**
    * default to today
    */
  def whoIsOn(scheduleId: String)(implicit ec: ExecutionContext): Future[Either[String, Seq[User]]] = {
    val now   = new DateTime
    val start = now
    // if start and end are the same, you don't any results back ... ?
    val end   = start.plusMinutes(1)
    whoIsOn(scheduleId, start, end)
  }

  def contactNumber(userId: String)(implicit ec: ExecutionContext):
      Future[Either[String, String]] =
    submitJson(s"users/$userId/contact_methods") map { response =>
      response.right.flatMap { json =>
        (json \ "contact_methods").validate[Seq[ContactMethod]] match {
          case JsSuccess(methods, _) =>
            // successfully deserialised a list of methods, now search for a phone type
            val numOpt = methods.collectFirst {
              case ContactMethod(_, "phone", Some(num)) => num
            }
            // if we found a phone number, return this is a Right, otherwise,
            // return a Left with an error
            numOpt.toRight("Could not find a phone number")
          case JsError(err)          => Left(err.toString)
        }
      }
    }
}

object PagerDutyAPI {
  lazy val default = new PagerDutyAPI(configuration.getString("pagerduty.token").get,
                                      configuration.getString("pagerduty.url").get)
}
