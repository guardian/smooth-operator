name := "operator"

version := "0.0.1-SNAPSHOT"

scalacOptions += "-language:implicitConversions"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "com.twilio.sdk" % "twilio-java-sdk" % "3.4.5",
  "com.typesafe.play" %% "play-ws" % "2.3.8"
)
