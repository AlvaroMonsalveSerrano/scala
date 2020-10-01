import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"

  lazy val cats_core = "org.typelevel" %% "cats-core" % "1.0.0-MF"

  lazy val scalacheck =  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test

  lazy val akka_actor = "com.typesafe.akka" %% "akka-actor" % "2.4.12"
}
