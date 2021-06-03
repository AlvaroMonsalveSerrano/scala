import Dependencies._
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName

ThisBuild / description := "Scala Basic Concept Testing and Documentation Examples"
ThisBuild / scalaVersion := "2.13.6"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "es.ams"
ThisBuild / organizationName := "AMS"
ThisBuild / developers := List(
  Developer(
    id = "",
    name = "Álvaro Monsalve Serrano",
    email = "",
    url = url("http://alvaromonsalve.com")
  )
)

lazy val basicScalacOptions = Seq(
  "-deprecation",
  "-encoding",
  "utf-8",
  "-explaintypes",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Xlint",
  "-Xfatal-warnings",
  "-language:reflectiveCalls",
  "-Yrangepos"
)

lazy val commonSettings = Seq(
  scalacOptions ++= basicScalacOptions,
  test in assembly := {},
  assemblyJarName in assembly := "scala-example.jar"
)

lazy val root = (project in file("."))
  .aggregate(cats)
  .aggregate(doobie)
  .aggregate(pf)
  .aggregate(catsEffect)
  .aggregate(catsFree)
  .aggregate(ciris)
  .aggregate(zio)
  .aggregate(http4s)
  .aggregate(mquill)
  .settings(BuildInfoSettings.value)
  .settings(
    name := "scala",
    commonSettings,
    scalacOptions ++= basicScalacOptions,
    libraryDependencies ++= commonDependencies,
    buildInfoOptions += BuildInfoOption.ToJson,
    resolvers ++= Seq(
      "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots"),
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )
  .enablePlugins(GitVersioning)
  .enablePlugins(BuildInfoPlugin)

lazy val commonDependencies = Seq(
  scalaTest,
  scalacheck,
  munit,
  munit_cats_effect_2
)

lazy val cats = (project in file("cats"))
  .settings(
    name := "example-cats",
    assemblySettings,
    scalacOptions ++= basicScalacOptions,
    libraryDependencies ++=
      catsDependencies ++ Seq(
        scalaTest
      )
  )

lazy val catsDependencies = Seq(
  cats_core,
  scalacheck,
  akka_actor
)

lazy val catsEffect = (project in file("cats-effect"))
  .settings(
    name := "example-cats-effect",
    assemblySettings,
    scalacOptions ++= basicScalacOptions,
    libraryDependencies ++=
      catsEffectDependencies ++ Seq(
        scalaTest
      )
  )

lazy val catsEffectDependencies = Seq(
  cats_effect,
  cats_effect_laws
)

lazy val catsFree = (project in file("cats-free"))
  .settings(
    name := "example-cats-free",
    assemblySettings,
    scalacOptions ++= basicScalacOptions,
    libraryDependencies ++=
      catsFreeDependencies ++
        doobieDependencies ++
        cirisDependencies ++
        catsEffectDependencies ++ Seq(
          scalaTest
        )
  )

lazy val catsFreeDependencies = Seq(
  cats_free
)

lazy val pf = (project in file("pf"))
  .settings(
    name := "pf",
    assemblySettings,
    scalacOptions ++= basicScalacOptions,
    libraryDependencies ++=
      Seq(
        scalaTest
      )
  )

lazy val doobie = (project in file("doobie"))
  .settings(
    name := "example-doobie",
    assemblySettings,
    scalacOptions ++= basicScalacOptions,
    libraryDependencies ++=
      doobieDependencies ++ Seq(
        scalaTest
      )
  )

lazy val doobieDependencies = Seq(
  doobie_core,
  doobie_h2,
  doobie_hikari,
  mysql_connector_java,
  monix,
  doobie_quill,
  doobie_spec2,
  doobie_scalatest
)

lazy val ciris = (project in file("ciris"))
  .settings(
    name := "example-ciris",
    assemblySettings,
    scalacOptions ++= basicScalacOptions,
    libraryDependencies ++=
      cirisDependencies ++ Seq(
        scalaTest
      )
  )

lazy val cirisDependencies = Seq(
  ciris_ciris,
  ciris_circe,
  ciris_enumeratum,
  ciris_refined,
  ciris_squants,
  ciris_refined_cats
)

lazy val zio = (project in file("zio"))
  .settings(
    name := "example-zio",
    assemblySettings,
    scalacOptions ++= basicScalacOptions,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++=
      zioDependencies ++ Seq(
        scalaTest
      )
  )

lazy val zioDependencies = Seq(
  zio_core,
  zio_streams,
  zio_test,
  zio_test_sbt,
  zio_test_magnolia
)

lazy val http4s = (project in file("http4s"))
  .settings(
    name := "example-http4s",
    assemblySettings,
    scalacOptions ++= basicScalacOptions,
    testFrameworks += new TestFramework("munit.Framework"),
    libraryDependencies ++=
      http4sDependencies ++ Seq(
        scalaTest,
        munit,
        munit_cats_effect_2
      )
  )

lazy val http4sDependencies = Seq(
  http4s_blaze_server,
  http4s_blaze_client,
  http4s_circe,
  http4s_dsl,
  circe_generic,
  circe_literal
)

lazy val mquill = (project in file("quill"))
  .settings(
    name := "example-quill",
    assemblySettings,
    scalacOptions ++= basicScalacOptions,
    testFrameworks += new TestFramework("munit.Framework"),
    unmanagedClasspath in Compile += baseDirectory.value / "src" / "main" / "resources",
    libraryDependencies ++=
      quillDependencies ++ Seq(
        scalaTest,
        munit,
        munit_cats_effect_2
      )
  )

lazy val quillDependencies = Seq(
  quill,
  quill_sql,
  quillJdbc,
  quillH2,
  quillPostgres,
  postgres
)

lazy val assemblySettings = Seq(
  assemblyJarName in assembly := name.value + ".jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case "application.conf"            => MergeStrategy.concat
    case x =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  }
)
