package es.ams.configurations

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import ciris._

import scala.concurrent.duration._

/** CIRIS
  * -----
  *
  * https://cir.is/docs/configurations
  *
  * APP_ENV=Local;DATABASE_USERNAME=user;DATABASE_PASSWORD=012345678901234567890123456789;API_PORT=8080;API_KEY=keyRR01234567890123456789;API_TIMEOUT=100 millis
  */
object Example1 extends IOApp {

  val port: ConfigValue[Int] =
    env("API_PORT").or(prop("api.port")).as[Int]

  val timeout: ConfigValue[Option[Duration]] =
    env("API_TIMEOUT").as[Duration].option

  def exampleLoadIntENV(): Unit = {

    val portResult: Int = port.load[IO].unsafeRunSync()
    println(s"Port=${portResult}")
    println()

  }

  def exampleLoadPairEnvVar(): Unit = {

    final case class Config(port: Int, tiemout: Option[Duration])

    val config: ConfigValue[Config] = (port, timeout).parMapN(Config)
    val resultConfig: Config        = config.load[IO].unsafeRunSync()
    println(s"Result Config->${resultConfig}")
    println()

  }

  def exampleLoadWithForCom(): Unit = {

    final case class Config(port: Int, tiemout: Option[Duration])

    val config = for {
      eport    <- env("API_PORT").or(prop("api.port")).as[Int]
      etimeout <- env("API_TIMEOUT").as[Duration].option
    } yield {
      Config(eport, etimeout)
    }
    val result: Config = config.load[IO].unsafeRunSync()
    println(s"Result Config->${result}")
    println()

  }

  def exampleDefaultValue1(): Unit = {
    val timeDefault: ConfigValue[Duration] =
      env("API_TIME_DEEFAULT").as[Duration].default(10.seconds)

    val result: Duration = timeDefault.load[IO].unsafeRunSync()
    println(s"Result default 1=${result}")
    println()

  }

  def exampleDefaultValue2(): Unit = {

    final case class Config(port: Int, tiemout: Option[Duration])

    val config = (
      env("API_PORT").or(prop("api.port")).as[Int],
      env("API_TIMEOUT").as[Duration].option
    ).parMapN(Config).default {
      Config(8082, 20.seconds.some)
    }

    val result: Config = config.load[IO].unsafeRunSync()
    println(s"Result default 2=${result}")
    println()

  }

  def exampleSecrets(): Unit = {
    val apiKey: ConfigValue[Secret[String]] = env("API_KEY").secret
    val resultSecret: Secret[String]        = apiKey.load[IO].unsafeRunSync()
    println(s"secret=${resultSecret.value}")
    println()

  }

  def exampleSource(): Unit = {

    def env(name: String): ConfigValue[String] = {
      ConfigValue.suspend {
        val key   = ConfigKey.env(name)
        val value = System.getenv(name)

        if (value != null) {
          ConfigValue.loaded(key, value)
        } else {
          ConfigValue.missing(key)
        }
      }
    }

    val resultEnv: ConfigValue[String] = env("API_PORT")
    val result: String                 = resultEnv.load[IO].unsafeRunSync()
    println(s"Source result=${result}")
    println()

  }

  override def run(args: List[String]): IO[ExitCode] = {
    exampleLoadIntENV()

    exampleLoadPairEnvVar()

    exampleLoadWithForCom()

    exampleDefaultValue1()

    exampleDefaultValue2()

    exampleSecrets()

    exampleSource()

    IO(println("End Example1")).as(ExitCode.Success)
  }

}
