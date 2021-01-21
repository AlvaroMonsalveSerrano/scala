package es.ams.jsonhandling

import cats._
//import cats.implicits._
import cats.syntax.all._
import cats.effect._

import io.circe._
//import io.circe.literal._
//import io.circe.syntax._

import org.http4s._
import org.http4s.dsl._
import org.http4s.circe._
//import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server._
import org.http4s.server.blaze._

//import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global

/** https://http4s.org/v0.21/json/
  *
  * Definición de un servicio en el que se define una entidad BusinessOperation para definir
  * los objetos de entrada y salida de una operación de negocio. L
  *
  * La funcionalidad de negocio se define y realiza en la misma entidad BusinessOperation.
  *
  * Comandos curl para probar los endpoint:
  *
  * 1.- http://localhost:7677/healthy
  * 2.- http://localhost:7677/api/business/pepe
  */
object JsonServerEjem2 extends IOApp {

  def healthyRoute[F[_]: Sync](): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root / "healthy" =>
      Ok("Ok!")
    }
  }

  def businessRoute[F[_]: Sync](business: BusinessOperation[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case req @ GET -> Root / "business" / nameParam =>
      for {
        responseBusiness <- business.run(BusinessOperation.Request(nameParam))
        result           <- Ok(responseBusiness)
      } yield (result)
    }
  }

  val httpApp = Router("/" -> healthyRoute[IO](), "/api" -> businessRoute[IO](BusinessOperation.run)).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(7677, "localhost")
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}

//
// Definición de la operación de negocio.
//
trait BusinessOperation[F[_]] {
  def run(request: BusinessOperation.Request): F[BusinessOperation.Response]
}
object BusinessOperation {

  implicit def apply[F[_]](implicit ev: BusinessOperation[F]): BusinessOperation[F] = ev

  final case class Request(param1: String) extends AnyVal

  final case class Response(message: String) extends AnyVal
  object Response {

    implicit val responseEncoder: Encoder[Response] = new Encoder[Response] {
      final def apply(obj: Response): Json = Json.obj(
        ("message", Json.fromString(obj.message))
      )
    }

    implicit def responseEntityEncoder[F[_]: Applicative]: EntityEncoder[F, Response] =
      jsonEncoderOf[F, Response]

  }

  def run[F[_]: Applicative]: BusinessOperation[F] = new BusinessOperation[F] {
    override def run(request: BusinessOperation.Request): F[BusinessOperation.Response] = Response(
      s"Business response. Param: ${request.param1}"
    ).pure[F]
  }

}
