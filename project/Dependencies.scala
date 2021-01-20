import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % Versions.scalatest % Test

  lazy val scalacheck = "org.scalacheck" %% "scalacheck" % Versions.scalacheck % Test

  lazy val cats_core = "org.typelevel" %% "cats-core" % Versions.cats_core

  lazy val cats_effect      = "org.typelevel" %% "cats-effect"      % Versions.cats_effect
  lazy val cats_effect_laws = "org.typelevel" %% "cats-effect-laws" % Versions.cats_effect % "test"
  lazy val cats_free        = "org.typelevel" %% "cats-free"        % Versions.cats_free

  lazy val akka_actor = "com.typesafe.akka" %% "akka-actor" % Versions.akka_actor

  lazy val doobie_core      = "org.tpolecat" %% "doobie-core"      % Versions.doobie
  lazy val doobie_h2        = "org.tpolecat" %% "doobie-h2"        % Versions.doobie
  lazy val doobie_hikari    = "org.tpolecat" %% "doobie-hikari"    % Versions.doobie
  lazy val doobie_postgres  = "org.tpolecat" %% "doobie-postgres"  % Versions.doobie_postgres
  lazy val doobie_quill     = "org.tpolecat" %% "doobie-quill"     % Versions.doobie
  lazy val doobie_spec2     = "org.tpolecat" %% "doobie-specs2"    % Versions.doobie % "test"
  lazy val doobie_scalatest = "org.tpolecat" %% "doobie-scalatest" % Versions.doobie % "test"

  lazy val mysql_connector_java = "mysql" % "mysql-connector-java" % Versions.mysql_connector

  lazy val monix = "io.monix" %% "monix" % Versions.monix

  lazy val ciris_ciris        = "is.cir"     %% "ciris"            % Versions.ciris
  lazy val ciris_circe        = "is.cir"     %% "ciris-circe"      % Versions.ciris
  lazy val ciris_enumeratum   = "is.cir"     %% "ciris-enumeratum" % Versions.ciris
  lazy val ciris_refined      = "is.cir"     %% "ciris-refined"    % Versions.ciris
  lazy val ciris_squants      = "is.cir"     %% "ciris-squants"    % Versions.ciris
  lazy val ciris_refined_cats = "eu.timepit" %% "refined-cats"     % Versions.refined_cats

  lazy val zio_core          = "dev.zio" %% "zio"               % Versions.zio
  lazy val zio_streams       = "dev.zio" %% "zio-streams"       % Versions.zio
  lazy val zio_test          = "dev.zio" %% "zio-test"          % Versions.zio % "test"
  lazy val zio_test_sbt      = "dev.zio" %% "zio-test-sbt"      % Versions.zio % "test"
  lazy val zio_test_magnolia = "dev.zio" %% "zio-test-magnolia" % Versions.zio % "test" // optional

  lazy val munit               = "org.scalameta" %% "munit"               % Versions.munit                     % Test
  lazy val munit_cats_effect_2 = "org.typelevel" %% "munit-cats-effect-2" % Versions.munit_cats_effect_version % Test

  lazy val http4s_blaze_server = "org.http4s" %% "http4s-blaze-server" % Versions.http4s
  lazy val http4s_blaze_client = "org.http4s" %% "http4s-blaze-client" % Versions.http4s
  lazy val http4s_dsl          = "org.http4s" %% "http4s-dsl"          % Versions.http4s
  lazy val http4s_circe        = "org.http4s" %% "http4s-circe"        % Versions.http4s
  lazy val circe_generic       = "io.circe"   %% "circe-generic"       % Versions.circe
  lazy val circe_literal       = "io.circe"   %% "circe-literal"       % Versions.circe

}
