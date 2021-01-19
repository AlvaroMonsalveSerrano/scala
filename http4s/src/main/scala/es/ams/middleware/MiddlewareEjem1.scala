package es.ams.middleware

import cats.data.Kleisli
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router

import scala.concurrent.ExecutionContext.Implicits.global

/** Referencia documental: https://http4s.org/v0.21/middleware/
  *
  * Definición de un wrapper de un servicio.
  *
  * Pruebas del servicio:
  *
  * 1.- curl -i http://localhost:7676/hello/pepe
  * 2.- curl http://localhost:7676/api/rest1
  */
object MiddlewareEjem1 extends IOApp {

  // Wraper de un servio.
  def myMiddle(service: HttpRoutes[IO], header: Header): HttpRoutes[IO] = Kleisli { (req: Request[IO]) =>
    service(req).map {
      case Status.Successful(resp) =>
        resp.putHeaders(header)
      case resp =>
        resp
    }
  }

  val service = HttpRoutes.of[IO] { case GET -> Root / "hello" / name =>
    Ok(s"Hello wrapper, $name.")
  }

  val wrappedService = myMiddle(service, Header("SomeKey", "SomeValue"))

  val apiService = HttpRoutes.of[IO] { case GET -> Root / "rest1" =>
    Ok("OK response API")
  }

  val httpRoute = Router("/" -> wrappedService, "/api" -> apiService).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(7676, "localhost")
      .withHttpApp(httpRoute)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

}
