package lib.pagerduty

import play.api.libs.json.{JsValue, Reads, JsSuccess}

object JsonImplicits {
  implicit val userReads = new Reads[User] {
    def reads(js: JsValue) = for {
      id <- (js \ "id").validate[String]
      email <- (js \ "email").validate[String]
    } yield new User(id, email)
  }

  implicit val contactMethodReads = new Reads[ContactMethod] {
    def reads(js: JsValue) = for {
      id <- (js \ "id").validate[String]
      typ <- (js \ "type").validate[String]
      num <- (js \ "phone_number").validate[Option[String]]
    } yield new ContactMethod(id, typ, num)
  }
}
