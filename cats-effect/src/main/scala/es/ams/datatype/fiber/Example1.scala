package es.ams.datatype.fiber

import cats.effect.{ContextShift, Fiber, IO}

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

/** Fiber
  * =====
  *
  * https://typelevel.org/cats-effect/datatypes/fiber.html
  *
  * Representa el resultado puro de un tipo de dato Async.
  *
  * Se puede pensar que un Fiber es como un hilo.
  */
object Example1 extends App {

  def example1(): Unit = {

    println(s"-*- Example1 -*-")

    // OJO! Importante para start.
    implicit val ctx = IO.contextShift(global)

    val io = IO(println("Hello world!!"))

    val fiber: IO[Fiber[IO, Unit]] = io.start

    fiber.unsafeRunAsyncAndForget()

  }

  def example2(): Unit = {

    println(s"-*- Example2 -*-")

    implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    // OJO!! IO define la mónada error.
    val launchMissiles: IO[Unit] = IO.raiseError(new Exception("Boom!!"))

    val runToBunker: IO[Unit] = IO(println("To the bunker!!!"))

    val result = for {
      fiber <- launchMissiles.start
      _ <- runToBunker.handleErrorWith { error =>
        fiber.cancel *> IO.raiseError(error)
      }
      aftermath <- fiber.join
    } yield (aftermath)

    result.unsafeRunAsyncAndForget() // Ejecución OK.
//    result.unsafeRunSync()               // Ejecución KO

  }

  example1()
  example2()

}
