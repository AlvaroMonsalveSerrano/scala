package es.ams.basic

import cats.effect.{IO} // ExitCode IOApp
import cats.implicits._
import ciris._
import ciris.refined._
import enumeratum.{CirisEnum, Enum, EnumEntry}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.collection.MinSize
import eu.timepit.refined.string.MatchesRegex
import eu.timepit.refined.types.net.UserPortNumber
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.W

import scala.concurrent.duration._

import scala.concurrent.ExecutionContext

/** CIRIS
  * -----
  *
  * https://cir.is/docs/quick-example
  *
  * Variables de entorno usadas de prueba:
  *
  * APP_ENV=Local;DATABASE_USERNAME=user;DATABASE_PASSWORD=012345678901234567890123456789;API_PORT=8080;API_KEY=keyRR01234567890123456789
  *
  * Ejemplo definido como App.
  */
object Example1Basic extends App {

  implicit val cs = IO.contextShift(ExecutionContext.global)

  sealed trait AppEnvironment extends EnumEntry

  object AppEnvironment extends Enum[AppEnvironment] with CirisEnum[AppEnvironment] {
    case object Local      extends AppEnvironment
    case object Testing    extends AppEnvironment
    case object Production extends AppEnvironment

    val values = findValues
  }

  import AppEnvironment.{Local, Testing, Production}

  type ApiKey = String Refined MatchesRegex[W.`"[a-zA-Z0-9]{25,40}"`.T]

  type DatabasePassword = String Refined MinSize[W.`30`.T]

  final case class ApiConfig(
      port: UserPortNumber,
      key: Secret[ApiKey],
      timeout: Option[FiniteDuration]
  )

  final case class DatabaseConfig(
      username: NonEmptyString,
      password: Secret[DatabasePassword]
  )

  final case class Config(
      appName: NonEmptyString,
      environment: AppEnvironment,
      api: ApiConfig,
      database: DatabaseConfig
  )

  def apiConfig(environment: AppEnvironment): ConfigValue[ApiConfig] =
    (
      env("API_PORT").or(prop("api.port")).as[UserPortNumber].option,
      env("API_KEY").as[ApiKey].secret
    ).parMapN { (port, key) =>
      ApiConfig(
        port = port getOrElse 9000,
        key = key,
        timeout = environment match {
          case Local | Testing => None
          case Production      => Some(10.seconds)
        }
      )
    }

  val databaseConfig: ConfigValue[DatabaseConfig] =
    (
      env("DATABASE_USERNAME").as[NonEmptyString].default("username"),
      env("DATABASE_PASSWORD").as[DatabasePassword].secret
    ).parMapN(DatabaseConfig)

  val config: ConfigValue[Config] =
    env("APP_ENV").as[AppEnvironment].flatMap { environment =>
      (
        apiConfig(environment),
        databaseConfig
      ).parMapN { (api, database) =>
        Config(
          appName = "my-api",
          environment = environment,
          api = api,
          database = database
        )
      }
    }

  val databaseLoad                         = databaseConfig.load[IO]
  val resultDatabaseConfig: DatabaseConfig = databaseLoad.unsafeRunSync()
  println(s"database result=${resultDatabaseConfig}")
  println()

  import AppEnvironment.Local
  val apiConfigLoad              = apiConfig(Local).load[IO]
  val resultApiConfig: ApiConfig = apiConfigLoad.unsafeRunSync()
  println(s"result ApiConfig=${resultApiConfig}")
  println()

  val configLoad     = config.load[IO]
  val result: Config = configLoad.unsafeRunSync()
  println(s"result Config=${result}")
  println()

}
