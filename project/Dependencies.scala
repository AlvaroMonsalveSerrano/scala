import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"

  lazy val scalacheck =  "org.scalacheck" %% "scalacheck" % "1.14.0" % Test

  lazy val cats_core = "org.typelevel" %% "cats-core" % "1.0.0-MF"

  lazy val cats_effect = "org.typelevel" %% "cats-effect" % "2.2.0"
  lazy val cats_effect_laws = "org.typelevel" %% "cats-effect-laws" % "2.2.0" % "test"


  lazy val akka_actor = "com.typesafe.akka" %% "akka-actor" % "2.4.12"

  lazy val doobie_core = "org.tpolecat" %% "doobie-core"  % "0.9.0"
  lazy val doobie_h2 = "org.tpolecat" %% "doobie-h2" % "0.9.0"  // H2 driver 1.4.200 + type mappings.
  lazy val doobie_hikari = "org.tpolecat" %% "doobie-hikari" % "0.9.0"  // HikariCP transactor.
  lazy val doobie_postgres = "org.tpolecat" %% "doobie-postgres" % "0.9.0"  // Postgres driver 42.2.12 + type mappings.
  lazy val doobie_quill = "org.tpolecat" %% "doobie-quill" % "0.9.0"  // Support for Quill 3.5.1
  lazy val doobie_spec2 = "org.tpolecat" %% "doobie-specs2" % "0.9.0" % "test" // Specs2 support for typechecking statements.
  lazy val doobie_scalatest = "org.tpolecat" %% "doobie-scalatest" % "0.9.0" % "test"  // ScalaTest support for typechecking statements.

  lazy val mysql_connector_java = "mysql" % "mysql-connector-java" % "5.1.34"

  lazy val monix = "io.monix" %% "monix" % "3.1.0"

}
