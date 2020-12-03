package es.ams.datatype.ioapp

//import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.ExitCase.Canceled
import cats.effect._
import scala.concurrent.duration._

/** https://typelevel.org/cats-effect/datatypes/ioapp.html
  *
  * Cancelation and Safe Resource Release
  *
  * Cuando IOApp recibe una señal de SIGABORT, SIGINT u otra interrupción esta puede ser capturada.
  *
  * Si se ejecuta con sbt hay que asegurarse que se comfigura como: forl := true
  *
  * IOApp es muy bueno para describir programas funcionales puros proporcionando una control de interrupciones.
  */
object Example3 extends IOApp {

  def loop(n: Int): IO[ExitCode] =
    IO.suspend {
      if (n < 10)
        IO.sleep(1.second) *> IO(println(s"Tick: ${n}")) *> loop(n + 1)
      else
        IO.pure(ExitCode.Success)
    }

  override def run(args: List[String]): IO[ExitCode] = {
    loop(0).guaranteeCase {
      case Canceled => IO(println("Interrupted: releasing and exiting!"))
      case _        => IO(println("Normal exit!"))
    }
  }

}
