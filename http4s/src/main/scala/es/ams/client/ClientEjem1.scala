package es.ams.client

import cats.effect._
import io.circe.{Decoder, Encoder}
import org.http4s.Status.{NotFound, Successful}
import org.http4s.circe._
import org.http4s.syntax.all._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import scala.concurrent.ExecutionContext.global

object ClientEjem1 extends IOApp {

  case class ResponseServer(message: String) extends AnyVal
  object ResponseServer {
    implicit val decoderUser: Decoder[ResponseServer] =
      Decoder.forProduct1("msg")(ResponseServer.apply)

    implicit val encoderUser: Encoder[ResponseServer] =
      Encoder.forProduct1("msg")(u => (u.message))
  }

  def runClient(client: Client[IO]): IO[Unit] = IO {

    val responseClient: IO[String] = client.get(uri"http://localhost:7676/healthy/aa") {
      case Successful(resp) => {
        resp.decodeJson[ResponseServer].map(_.toString)
      }
      case NotFound(_) => IO.pure("Not found!!")
      case resp        => IO.pure("Failed " + resp.status)
    }

    println(s"Result server=${responseClient.unsafeRunSync()}")
  }

  def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO](global).resource.use(runClient).as(ExitCode.Success)

}
