package es.ams.client

import cats.effect._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

import io.circe._
import io.circe.literal._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global

/** Referencia: https://http4s.org/v0.21/streaming/
  *
  * Las secuencias se devuelven al cliente como respuestas HTTP fragmentadas automáticamente.
  *
  * StreamingEjem1 define un servicio de Stream que cada segundo responde un string con el
  * número del segundo desde su inicio.
  */
object ServiceToClientEjem extends IOApp {

  case class Response(message: String) extends AnyVal
  object Response {
    implicit val userEncoder: Encoder[Response] =
      Encoder.instance { (response: Response) =>
        json"""{"msg": ${response.message}}"""
      }
  }

  val healthyService = HttpRoutes.of[IO] { case GET -> Root / "healthy" / name =>
    Ok(Response(name).asJson)
  }

  val httpRoute = Router("/" -> healthyService).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(7676, "localhost")
      .withHttpApp(httpRoute)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)

}
