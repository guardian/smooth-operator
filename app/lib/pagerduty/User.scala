package lib.pagerduty

class User(val id: String,
           val email: String)

class UserWithNumber(val id: String,
                     val email: String,
                     val phone: String) extends User(id, email)
