package es.ams.concurrency.deferred

import cats.effect.IO
import cats.effect.concurrent.Deferred
import cats.syntax.all._
import scala.concurrent.ExecutionContext


/**
  * https://typelevel.org/cats-effect/concurrency/deferred.html
  *
  * Deferred es una primitiva de sincronozación que representa un valor único que puede no estar todavía disponible.
  *
  * El escenario de uso es aquel que muchos procesos pueden modificar el mismo valor pero solo importa el primero
  * en hacerlo y detenga el procesamiento.
  *
  */
object Example1 extends App {

  def example1(): Unit = {

    implicit val cs = IO.contextShift(ExecutionContext.global)

    def start(d: Deferred[IO, Int]): IO[Unit] = {

      val attemptCompletion: Int => IO[Unit] =
        n => d.complete(n).attempt.void

      List(
        IO.race(attemptCompletion(1), attemptCompletion(2)),
        d.get.flatMap { n => IO(println(show"Result: $n")) }
      ).parSequence.void

    }

    val program: IO[Unit] = for {
      d <- Deferred[IO, Int]
      _ <- start(d)
    } yield ()

    program.unsafeRunSync()

  }

  example1()

}
