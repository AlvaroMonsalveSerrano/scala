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
  lazy val zio_interop_cats  = "dev.zio" %% "zio-interop-cats"  % "2.1.4.0"
  lazy val zio_streams       = "dev.zio" %% "zio-streams"       % Versions.zio
  lazy val zio_test          = "dev.zio" %% "zio-test"          % Versions.zio % "test"
  lazy val zio_test_sbt      = "dev.zio" %% "zio-test-sbt"      % Versions.zio % "test"
  lazy val zio_test_magnolia = "dev.zio" %% "zio-test-magnolia" % Versions.zio % "test" // optional
  lazy val zio_logging       = "dev.zio" %% "zio-logging"       % Versions.zio_logging
  lazy val zio_logging_slf4j = "dev.zio" %% "zio-logging-slf4j" % Versions.zio_logging

  lazy val zio_config          = "dev.zio" %% "zio-config"          % Versions.zio_config
  lazy val zio_config_magnolia = "dev.zio" %% "zio-config-magnolia" % Versions.zio_config
  lazy val zio_config_typesafe = "dev.zio" %% "zio-config-typesafe" % Versions.zio_config // module (HOCON/Json source)
  lazy val zio_config_refined  = "dev.zio" %% "zio-config-refined"  % Versions.zio_config
  lazy val zio_config_yaml     = "dev.zio" %% "zio-config-yaml"     % Versions.zio_config // module (Yaml source)

  lazy val munit               = "org.scalameta" %% "munit"               % Versions.munit                     % Test
  lazy val munit_cats_effect_2 = "org.typelevel" %% "munit-cats-effect-2" % Versions.munit_cats_effect_version % Test

  lazy val http4s_blaze_server = "org.http4s" %% "http4s-blaze-server" % Versions.http4s
  lazy val http4s_blaze_client = "org.http4s" %% "http4s-blaze-client" % Versions.http4s
  lazy val http4s_dsl          = "org.http4s" %% "http4s-dsl"          % Versions.http4s
  lazy val http4s_circe        = "org.http4s" %% "http4s-circe"        % Versions.http4s

  lazy val postgres = "org.postgresql" % "postgresql" % "42.2.8"

  lazy val quillH2 = "com.h2database" % "h2" % Versions.quill_h2

  lazy val quill         = "io.getquill" %% "quill-sql-portable"   % Versions.quill
  lazy val quill_sql     = "io.getquill" %% "quill-sql"            % Versions.quill
  lazy val quillJdbc     = "io.getquill" %% "quill-jdbc"           % Versions.quill
  lazy val quillPostgres = "io.getquill" %% "quill-async-postgres" % Versions.quill

  lazy val circe_core    = "io.circe" %% "circe-core"    % Versions.circe
  lazy val circe_generic = "io.circe" %% "circe-generic" % Versions.circe
  lazy val circe_literal = "io.circe" %% "circe-literal" % Versions.circe
  lazy val circe_parser  = "io.circe" %% "circe-parser"  % Versions.circe

  lazy val testcontainers_scalatest =
    "com.dimafeng" %% "testcontainers-scala-scalatest" % Versions.testcontainers % "test"
  lazy val testcontainers_munit =
    "com.dimafeng" %% "testcontainers-scala-munit" % Versions.testcontainers % "test"
  lazy val testcontainers_postgresql =
    "com.dimafeng" %% "testcontainers-scala-postgresql" % Versions.testcontainers % "test"

  lazy val testcontainers_scala_nginx =
    "com.dimafeng" %% "testcontainers-scala-nginx" % Versions.testcontainers % "test"


  lazy val slf4j_api = "org.slf4j" % "slf4j-api" % Versions.slf4jVersion

  lazy val logback_classic          = "ch.qos.logback"       % "logback-classic"          % "1.2.5"
  lazy val logstash_logback_encoder = "net.logstash.logback" % "logstash-logback-encoder" % "6.6"         
}
