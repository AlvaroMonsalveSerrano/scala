package es.ams.typeclass.effect

import cats.effect.{Effect, IO, SyncIO}


/**
  * Effect
  * ------
  *
  * Reference: https://typelevel.org/cats-effect/typeclasses/effect.html
  *
  */
object Example1 extends App {

  // def runAsync[A](fa: F[A])(cb: Either[Throwable, A] => IO[Unit]): SyncIO[Unit]
  def example1(): Unit = {

    println(s"-*- Example1 -*-")

    val operation1 = IO( println("Doing Operation1 ...") )

    val ioa1: SyncIO[Unit] = Effect[IO].runAsync(operation1){ cb =>
      cb match {
        case Right(value) => IO( println(s"Result Succes=${value}"))
        case Left(exception) => IO.raiseError(exception)
      }
    }

    ioa1.unsafeRunSync()

  }

  example1()

}
