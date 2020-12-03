import Dependencies._
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName

ThisBuild / description := "Scala Basic Concept Testing and Documentation Examples"
ThisBuild / scalaVersion := "2.13.4"
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
  "-language:reflectiveCalls"
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
    )
  )
  .enablePlugins(GitVersioning)
  .enablePlugins(BuildInfoPlugin)

lazy val commonDependencies = Seq(
  scalaTest,
  scalacheck
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
