package lib.pagerduty

import play.api.libs.json.{JsValue, Reads, JsSuccess}

object JsonImplicits {
  implicit val userReads = new Reads[User] {
    def reads(js: JsValue) = for {
      id <- (js \ "id").validate[String]
      email <- (js \ "email").validate[String]
    } yield new User(id, email)
  }
}
