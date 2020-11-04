package es.ams.concurrency.basic

import cats.effect.{Blocker, ContextShift, IO}

import scala.concurrent.ExecutionContext

/**
  * Concurrency
  * -----------
  *
  * https://typelevel.org/cats-effect/concurrency/basics.html
  *
  * Ejemplo de bloqueo de un hilo. Como norma, no se debe de bloquear un hilo pero, hay ocasiones que se debe de
  * bloquear. Esta operación puede ser peligroso y es mejor usar un grupo de subprocesos dedicados para bloquear
  * operaciones.
  *
  * Se usa Blocker[IO] para gestionar de forma segura operaciones de bloqueo de forma explícita.
  *
  */
object Example2 extends App {

  def example1(): Unit = {
    println(s"-*- Example1 -*-")

    implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

    def readName(): IO[String] = {
      IO{
        println(s"Name:")
        scala.io.StdIn.readLine()
      }
    }

    def doSomething(): IO[Unit] = IO( println(s"Saludo al mundo") )

    val program = Blocker[IO].use{ blocker =>
      for{
        _ <- blocker.blockOn(readName())
        _ <- doSomething()
      } yield ()
    }

    program.unsafeRunSync()

  }

  example1()

}
