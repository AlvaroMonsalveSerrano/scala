package es.ams.middleware

import cats.effect.{ExitCode, IO, IOApp}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.Implicits.global

/** Referencia documental: https://http4s.org/v0.21/middleware/
  *
  * Definición de un wrapper de un servicio utilizando una object.
  *
  * Pruebas del servicio:
  *
  * 1.- curl -i http://localhost:7676/middleware/pepe
  * 2.- curl http://localhost:7676/api/rest1
  */
object MiddlewareEjem2 extends IOApp {

  // Wraper de un servio definido como un object.
  object MyMiddle {

    def addHeader(resp: Response[IO], header: Header): Response[IO] = resp match {
      case Status.Successful(resp) => resp.putHeaders(header)
      case resp                    => resp
    }

    def apply(service: HttpRoutes[IO], header: Header) =
      service.map(addHeader(_, header))

  }

  val service = HttpRoutes.of[IO] { case GET -> Root / "middleware" / name =>
    Ok(s"Hello wrapper, $name.")
  }

  val wrappedService = MyMiddle(service, Header("SomeKey", "SomeValue"))

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
