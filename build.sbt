name := "operator"

version := "0.0.1-SNAPSHOT"

scalacOptions += "-language:implicitConversions"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DockerPlugin)

libraryDependencies += "com.twilio.sdk" % "twilio-java-sdk" % "3.4.5"
