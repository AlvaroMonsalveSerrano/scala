import Dependencies._
//import net.virtualvoid.sbt.graph.Plugin.graphSettings


// "2.12.11"
ThisBuild / scalaVersion     := "2.12.11"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "es.ams"
ThisBuild / organizationName := "AMS"

lazy val basicScalacOptions = Seq(
  "-deprecation",
  "-encoding",
  "utf-8",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Xlint",               // enable handy linter warnings
  "-Xfatal-warnings",     // turn compiler warnings into errors
  "-Ypartial-unification" // allow the compiler to unify type constructors of different arities

)

 lazy val root = (project in file("."))
   .aggregate(cats)
   .aggregate(doobie)
   .aggregate(pf)
   .aggregate(catsEffect)
   .aggregate(catsFree)
   .settings(
     name := "scala",
     scalacOptions ++= basicScalacOptions,
     libraryDependencies += scalaTest % Test,
     resolvers ++= Seq(
       "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
       Resolver.sonatypeRepo("releases"),
       Resolver.sonatypeRepo("snapshots"),
       "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
     )
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


// Uncomment the following for publishing to Sonatype.
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for more detail.

// ThisBuild / description := "Some descripiton about your project."
// ThisBuild / licenses    := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
// ThisBuild / homepage    := Some(url("https://github.com/example/project"))
// ThisBuild / scmInfo := Some(
//   ScmInfo(
//     url("https://github.com/your-account/your-project"),
//     "scm:git@github.com:your-account/your-project.git"
//   )
// )
// ThisBuild / developers := List(
//   Developer(
//     id    = "Your identifier",
//     name  = "Your Name",
//     email = "your@email",
//     url   = url("http://your.url")
//   )
// )
// ThisBuild / pomIncludeRepository := { _ => false }
// ThisBuild / publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
// }
// ThisBuild / publishMavenStyle := true
