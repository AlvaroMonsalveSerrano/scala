package es.ams.datatype.contextshift

import cats.effect.{ContextShift, IO, Sync}
import cats.syntax.all._

import scala.concurrent.ExecutionContext

/** ContextShift
  * ------------
  *
  * https://typelevel.org/cats-effect/datatypes/contextshift.html
  *
  * Equivale a:
  *  + Scala's ExecutionContext
  *  + Java's Executor
  *
  *  ContextShift no es una type class como tal.
  */
object ExampleShift extends App {

  /** La operación Shift es un efecto que lanza un fork lógico.
    */
  def example1(): Unit = {

    println(s"-*- Example1 -*-")

    implicit val contextShift: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
    implicit val F                              = implicitly[Sync[IO]]

    def suma10[F[_]](n: Int, acc: Int)(implicit F: Sync[F], cs: ContextShift[F]): F[Int] = {
      F.suspend {
        val next =
          if (n < 10)
            suma10(n + 1, acc + n)
          else
            F.pure(acc)

        if (n % 5 == 0)
          cs.shift *> next // lanza un fork
        else
          next
      }
    }

    val runSuma: IO[Int] = suma10(1, 0)
    val result: Int      = runSuma.unsafeRunSync()
    println(s"->${result}")

  }

  example1()

}
