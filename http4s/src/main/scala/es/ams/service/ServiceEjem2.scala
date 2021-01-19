package es.ams.service

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global

import cats.syntax.all._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.Router

import io.circe.{Encoder, Json}
import org.http4s.circe._

/** https://http4s.org/v0.21/service/
  *
  * Definición de dos servicios: helloWorldService, servicio básico tipo "Hello World"; y, tweetService, servicio
  * con sos funciones con respuesta de JSON.
  *
  * Las rutas de los servicios son respectivamente: '/', para helloWorldService; y, '/api', para tweetService.
  *
  * Para probar:
  *
  * 1.- curl http://localhost:7676/hello/Pete
  * 2.- curl http://localhost:7676/api/tweets/1
  * 3.- curl http://localhost:7676/api/tweets/popular
  */

object ServiceEjem2 extends IOApp {

  // Definition of the service
  val helloWorldService = HttpRoutes.of[IO] { case GET -> Root / "hello" / name =>
    Ok(s"Hello, $name.")
  }

  case class Tweet(id: Int, message: String)

  implicit val tweetEncoder: Encoder[Tweet] = new Encoder[Tweet] {
    final def apply(a: Tweet): Json = Json.obj(
      ("message", Json.fromString(a.message))
    )
  }

  implicit val tweetSeqEncoder: Encoder[Seq[Tweet]] = new Encoder[Seq[Tweet]] {
    final def apply(a: Seq[Tweet]): Json = Json.obj(
      ("message1", Json.fromString("Test1")),
      ("message2", Json.fromString("Test2"))
    )
  }

  implicit def tweetEntityEncoder: EntityEncoder[IO, Tweet]       = jsonEncoderOf[IO, Tweet]
  implicit def tweetsEntityEncoder: EntityEncoder[IO, Seq[Tweet]] = jsonEncoderOf[IO, Seq[Tweet]]

  def getTweet(tweetId: Int): IO[Tweet] = IO(Tweet(id = 1, message = "Tweet1"))
  def getPopularTweets(): IO[Seq[Tweet]] = IO(
    List(Tweet(id = 2, message = "Tweet2"), Tweet(id = 3, message = "Tweet3"))
  )

  // Routes
  val tweetService = HttpRoutes.of[IO] {
    case GET -> Root / "tweets" / "popular" =>
      getPopularTweets().flatMap(Ok(_))

    case GET -> Root / "tweets" / IntVar(tweetId) =>
      getTweet(tweetId).flatMap(Ok(_))
  }

  val services = tweetService <+> helloWorldService

  val httpApp = Router("/" -> helloWorldService, "/api" -> services).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(7676, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

}
