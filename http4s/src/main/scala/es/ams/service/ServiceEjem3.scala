package es.ams.service

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.Implicits.global

/** https://http4s.org/v0.21/service/
  *
  * Definición de una servidor como recurso. Definición de un servicio: helloWorldService, servicio básico tipo
  * "Hello World".
  *
  * Las rutas de los servicios son respectivamente: '/', para helloWorldService.
  *
  * Para probar:
  *
  * 1.- curl http://localhost:7676/hello/Pete
  */

object ServiceEjem3 extends IOApp {

  val helloWorldService = HttpRoutes.of[IO] { case GET -> Root / "hello" / name =>
    Ok(s"Hello, $name.")
  }

  val httpApp = Router("/" -> helloWorldService).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(7676, "localhost")
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

}
