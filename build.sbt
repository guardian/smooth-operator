name := "operator"

version := "0.0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += "com.twilio.sdk" % "twilio-java-sdk" % "3.4.5"
