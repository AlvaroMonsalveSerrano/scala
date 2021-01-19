package es.ams.streaming

import fs2.Stream

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/** Referencia: https://http4s.org/v0.21/streaming/
  *
  * Las secuencias se devuelven al cliente como respuestas HTTP fragmentadas automáticamente.
  *
  * StreamingEjem1 define un servicio de Stream que cada segundo responde un string con el
  * número del segundo desde su inicio.
  */
object StreamingEjem1 extends IOApp {

  val seconds = Stream.awakeEvery[IO](1.second)

  val healthyService = HttpRoutes.of[IO] { case GET -> Root / "healthy" / name =>
    Ok(s"Healthy, $name.")
  }

  val service = HttpRoutes.of[IO] { case GET -> Root / "seconds" =>
    Ok(seconds.map(_.toString)) // map toString porque no hay un Encoder de Duration.
  }

  val httpRoute = Router("/" -> healthyService, "/api" -> service).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(7676, "localhost")
      .withHttpApp(httpRoute)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

}
