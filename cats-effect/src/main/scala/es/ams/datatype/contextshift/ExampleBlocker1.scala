package es.ams.datatype.contextshift

import cats.effect.{Blocker, ContextShift, ExitCode, IO, IOApp, Sync}

/** Blocker
  * -------
  *
  * https://typelevel.org/cats-effect/datatypes/contextshift.html
  *
  * Blocker proporciona un ExecutionContext que está destinado a ejecutar tareas de bloqueo y se integra diirectamente
  * con ContextShift.
  */
object ExampleBlocker1 extends IOApp {

  def readName[F[_]: Sync: ContextShift](blocker: Blocker): F[String] =
    blocker.delay {
      println(s"Name: ")
      scala.io.StdIn.readLine()
    }

  override def run(args: List[String]): IO[ExitCode] = {

    // Definición del contexto con Blocker.
    val name = Blocker[IO].use { blocker =>
      readName[IO](blocker)
    }

    for {
      n <- name
      _ <- IO(println(s"Hi, $n"))
    } yield {
      ExitCode.Success
    }

  }

}
