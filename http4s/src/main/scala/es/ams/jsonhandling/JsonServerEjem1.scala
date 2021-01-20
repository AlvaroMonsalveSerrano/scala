package es.ams.jsonhandling

import cats.data.Chain
import cats.effect._
import io.circe._
import io.circe.literal._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.server._
import org.http4s.server.blaze._
import org.http4s.implicits._

import scala.concurrent.ExecutionContext.Implicits.global

/** https://http4s.org/v0.21/json/
  *
  * Definición de un servicio métodos HTTP GET y POST.
  * Tratamiento de respuestas JSON y case class.
  *
  * Comandos curl para probar los endpoint:
  *
  * + curl -i http://localhost:7676/json2/pepe
  * + curl -i http://localhost:7676/json3/pepe
  * + curl -i http://localhost:7676/hello1/pepe/20
  * + curl -d "field1=value1&field2=value2"  -X POST http://localhost:7676/hello2
  */
object JsonServerEjem1 extends IOApp {

  case class User(name: String) extends AnyVal
  object User {
    implicit val userEncoder: Encoder[User] =
      Encoder.instance { (hello: User) =>
        json"""{"field": ${hello.name}}"""
      }
  }

  def hello(name: String): Json = json"""{"hello": $name}"""

  def getValueFromChain[A](chain: Chain[A]): String = chain match {
    case Chain(a) => a.toString
    case _        => ""
  }

  import User._
  val jsonService = HttpRoutes.of[IO] {
    case GET -> Root / "json2" / name => Ok(hello(name).toString())
    case GET -> Root / "json3" / name => Ok(User(name).asJson)
    case req @ GET -> Root / "hello1" / pName / edad =>
      for {
        user <- IO(User(name = pName + edad))
        resp <- Ok(User(user.name).asJson)
      } yield (resp)
    case req @ POST -> Root / "hello2" =>
      req.decode[UrlForm] { m =>
        val field1 = getValueFromChain(m.values.get("field1").head)
        val field2 = getValueFromChain(m.values.get("field2").head)
        Ok(User(s"$field1 $field2").asJson)
      }
  }

  val services = jsonService

  val httpApp = Router("/" -> services).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO](global)
      .bindHttp(7676, "localhost")
      .withHttpApp(httpApp)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
