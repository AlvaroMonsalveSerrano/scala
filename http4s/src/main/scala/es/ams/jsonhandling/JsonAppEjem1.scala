package es.ams.jsonhandling

// Server
import cats.effect._
import fs2.Stream
import org.http4s.client.blaze._

import scala.concurrent.ExecutionContext.global

// Resource
import cats.Applicative
import cats.effect.Sync
import cats.implicits._
import io.circe.Json
import io.circe.{Decoder, Encoder}

// Router
import org.http4s.dsl._
import org.http4s.server.blaze._

import org.http4s._
import org.http4s.implicits._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.circe._
import org.http4s.Method._

/** Definición de la aplicación que defien un server con dos endpoint: healthy, check de estado; y,
  * operation , define una operación que emplea un cliente.
  *
  * Para realizar pruebas desde la consola una vez arrancada la aplicación:
  *
  * + http://localhost:7676/healthy
  *   Resultado: Ok!
  *
  * + http://localhost:7676/operationResource
  *   Resultado: {"message":"Response Operation [param:Param1Test]: Ok!"}
  */
object JsonAppEjem1 extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    JsonAppServer.stream[IO].compile.drain.as(ExitCode.Success)
}

/** Definición del server.
  */
object JsonAppServer {
  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      resourceOperation = Resource.run[F](client) //

      httpApp = (ResourceRoutes.resourceRoute[F](resourceOperation)).orNotFound

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(7676, "localhost")
        .withHttpApp(httpApp)
        .serve

    } yield (exitCode)
  }.drain
}

/** Definición del enrrutador del recurso Resource del servicio.
  */
object ResourceRoutes {
  def resourceRoute[F[_]: Sync](resource: Resource[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "healthy" =>
        Ok("Ok!")
      case GET -> Root / "operationResource" =>
        for {
          resultBusiness <- resource.run(Resource.ResourceRequest(param1 = "Param1Test"))
          result         <- Ok(resultBusiness)
        } yield (result)
    }
  }
}

/** Definición de la  funcionalidad de negocio, objetos de entrada y salida de los endpoint del
  * servicio.
  *
  * Este recurso utiliza un cliente Http4s para conectarse a un sistema externo.
  *
  * @tparam F
  */
trait Resource[F[_]] {
  def run(param: Resource.ResourceRequest): F[Resource.ResourceResponse]
}
object Resource {
  def apply[F[_]](implicit ev: Resource[F]): Resource[F] = ev

  // Request
  final case class ResourceRequest(param1: String) extends AnyVal
  object ResourceRequest {
    implicit val resourceDecoder: Decoder[ResourceRequest] =
      Decoder.forProduct1("param1")(ResourceRequest.apply)

    implicit def resourceEntityDecoder[F[_]: Sync]: EntityDecoder[F, ResourceRequest] =
      jsonOf[F, ResourceRequest]

    implicit val resourceEncoder: Encoder[ResourceRequest] = new Encoder[ResourceRequest] {
      override final def apply(a: ResourceRequest): Json = Json.obj(
        ("param1", Json.fromString(a.param1))
      )
    }

    implicit def resourceEntityEncoder[F[_]: Applicative]: EntityEncoder[F, ResourceRequest] =
      jsonEncoderOf[F, ResourceRequest]
  }

  // Response
  final case class ResourceResponse(message: String) extends AnyVal
  object ResourceResponse {
    implicit val resourceDecoder: Decoder[ResourceResponse] =
      Decoder.forProduct1("message")(ResourceResponse.apply)

    implicit def resourceEntityDecoder[F[_]: Sync]: EntityDecoder[F, ResourceResponse] =
      jsonOf[F, ResourceResponse]

    implicit val resourceEncoder: Encoder[ResourceResponse] = new Encoder[ResourceResponse] {
      override final def apply(a: ResourceResponse): Json = Json.obj(
        ("message", Json.fromString(a.message))
      )
    }

    implicit def resourceEntityEncoder[F[_]: Applicative]: EntityEncoder[F, ResourceResponse] =
      jsonEncoderOf
  }

  // Exception
  final case class ResourceError(exp: Throwable) extends RuntimeException

  // Definición de la funcionalidad para la entidad Resource.
  def run[F[_]: Sync](client: Client[F]): Resource[F] = new Resource[F] {
    val dsl = new Http4sClientDsl[F] {}
    import dsl._
    override def run(param: ResourceRequest): F[ResourceResponse] = {
      val result: F[String] = client
        .expect[String](GET(uri"http://localhost:7676/healthy"))
        .adaptError { case excep =>
          ResourceError(excep)
        }

      result.map(elem => ResourceResponse(message = s"Response Operation [param:${param.param1}]: " + elem))
    }
  }

}
