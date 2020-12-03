package es.ams.concurrency.basic

import java.util.concurrent.Executors
import cats.effect.{ContextShift, Fiber, IO}
import scala.concurrent.ExecutionContext

/** Concurrency
  * -----------
  *
  * https://typelevel.org/cats-effect/concurrency/basics.html
  */
object Example1 extends App {

  // Definición de dos grupos de subprocesos.
  val ecOne = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())
  val ecTwo = ExecutionContext.fromExecutor(Executors.newSingleThreadExecutor())

  // Definición de dos contextos de ejeecución asociados a dos grupos de subprocesos.
  val contextShiftOne: ContextShift[IO] = IO.contextShift(ecOne)
  val contextShiftTwo: ContextShift[IO] = IO.contextShift(ecTwo)

  // definición de un hilo.
  def infiniteIO(id: Int)(cs: ContextShift[IO]): IO[Fiber[IO, Unit]] = {

    // El hilo no suelta el subproceso. No hace shift. Se hace infinito.
    def repeat: IO[Unit] = IO(println(id)).flatMap(_ => repeat)

    repeat.start(cs)

  }

  def infiniteIONoBlock(id: Int)(implicit cs: ContextShift[IO]): IO[Fiber[IO, Unit]] = {

    // El hilo suelta el subproceso con shift.
    def repeat: IO[Unit] = IO(println(id)).flatMap(_ => IO.shift *> repeat)

    repeat.start(cs)

  }

  def example1(): Unit = {

    println(s"-*- Example1 -*-")

    val program = for {
      _ <- infiniteIO(1)(contextShiftOne)
      _ <- infiniteIO(11)(contextShiftOne)
      _ <- infiniteIO(2)(contextShiftTwo)
      _ <- infiniteIO(22)(contextShiftTwo)
    } yield {}

    program.unsafeRunSync()

  }

  def example2(): Unit = {

    println(s"-*- Example1 -*-")

    val program = for {
      _ <- infiniteIONoBlock(1)(contextShiftOne)
      _ <- infiniteIONoBlock(11)(contextShiftOne)
      _ <- infiniteIONoBlock(2)(contextShiftTwo)
      _ <- infiniteIONoBlock(22)(contextShiftTwo)
    } yield {}

    program.unsafeRunSync()

  }

//  example1() // Se queda infinito escibiendo 1 y 2.
  example2() // Escribe todos los valores en un bucle.

}
