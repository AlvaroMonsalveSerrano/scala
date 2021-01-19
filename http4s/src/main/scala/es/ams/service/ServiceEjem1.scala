package es.ams.service

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import scala.concurrent.ExecutionContext.Implicits.global

import org.http4s.server.blaze._
import org.http4s.implicits._

/** https://http4s.org/v0.21/service/
  *
  * Definición de un servicio como una App.
  *
  * Para probar:
  *
  * curl http://localhost:8080/hello/Pete
  */

object ServiceEjem1 extends IOApp {

  val helloWorldService = HttpRoutes
    .of[IO] { case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
    }
    .orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

}
