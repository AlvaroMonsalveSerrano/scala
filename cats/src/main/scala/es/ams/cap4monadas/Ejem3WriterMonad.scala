package es.ams.cap4monadas

import cats.data.Writer

object Ejem3WriterMonad extends App {

  def slowly[A](body: => A): A =
    try body
    finally Thread.sleep(100)

  def factorial(n: Int): Int = {
    val ans = slowly(if (n == 0) 1 else n * factorial(n - 1))
    println(s"Factorial $n $ans")
    ans
  }

  /** Mono hilo
    */
  def ejemploMonoHilo(): Unit = {
    println(s"-- Cálculo del factorial de un número de forma mono-hilo")
    factorial(5)
  }

  /** Multi hilo
    */
  def ejemploMultiHilo(): Unit = {
    import scala.concurrent._
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._

    println(s"-- Ejecución multi-hilo --")
    Await.result(
      Future.sequence(
        Vector(
          Future(factorial(3)),
          Future(factorial(4))
        )
      ),
      5.seconds
    )
  }

  def factorialWriterMonad(): Unit = {

    import cats.syntax.writer._
    import cats.syntax.applicative._
    import cats.instances.vector._

    type Logged[A] = Writer[Vector[String], A]

    def slowly[A](body: => A) =
      try body
      finally Thread.sleep(100)

    def factorialWM(n: Int): Logged[Int] = {
      val result = for {
        ans <-
          if (n == 0) {
            1.pure[Logged]
          } else {
            slowly(factorialWM(n - 1).map(_ * n))
          }
        _ <- Vector(s"Factorial $n $ans").tell
      } yield { ans }

      result
    }

    val (log, result) = factorialWM(5).run
    println(s"4.1 Log=${log}")
    println(s"4.2 Result=${result}")
    println()

  }
//
//  ejemploMonoHilo()
//  ejemploMultiHilo()
  factorialWriterMonad()

}
