package es.ams.datatype.io

import cats.effect.IO
//import cats.syntax.all._

/** Type class IO
  * -------------
  *
  * Ref: https://typelevel.org/cats-effect/datatypes/io.html
  *
  * Un valor de tipo IO[A] es una computación que , cuando se evalua, puede realizar efectos antes que retorne un valor.
  *
  * El valor IO es puro, inmutable y preserva la trasnsparencia referencial siendo utilizable en la PF.
  * UN dato IO es una estructura de datos que representa una descripción de un efecto de lado.
  *
  * Puede ser síncrono y asíncrono.
  *
  * La computación:
  *
  * - la computación del programa retorna un resultado.
  * - Si hay error se gestiona como una Mónada error.
  * - Se peude cancelar, operación dependiente del usuario.
  */
object Example1IO extends App {

  def example1(): Unit = {

    println(s"-*- Example1 -*-")
    val ioa = IO { println("Hola Mundo!") }

    val program: IO[Unit] =
      for {
        _ <- ioa
        _ <- ioa
      } yield ()

    program.unsafeRunSync()

  }

  /** El tipo de datos IO conserva la transparencia referencial incluso cuando se trata de efectos secundarios y se
    * evalúa de forma perezosa. En un lenguaje como Scala, esta es la diferencia entre un resultado y la función que lo
    * produce.
    */
  def example2(): Unit = {

    println(s"-*- Example2 -*-")

    def doSometing(value: Int): IO[Unit] = {
      IO { println(s" Value: ${value}") }
    }

    val program1 = for {
      _ <- doSometing(20)
      _ <- doSometing(30)
    } yield ()
    program1.unsafeRunSync()

    // Ejemplo transparencia referencial.
    val task = doSometing(40)
    val program2 = for {
      _ <- task
      _ <- task
    } yield ()
    program2.unsafeRunSync()

  }

  /** Stack safety.
    * Es seguro realizar llamadas a flatMap en llamadas recursivas.
    */
  def example3(): Unit = {

    println(s"-*- Example 3 -*-")

    def fib(n: Int, a: Long = 0, b: Long = 1): IO[Long] =
      IO(a + b).flatMap { b2 =>
        if (n > 0)
          fib(n - 1, b, b2)
        else
          IO.pure(a)
      }

    val program1 =
      for {
        result <- fib(10)
        _      <- IO(println(s"Result fib(10)=${result}"))
      } yield ()
    program1.unsafeRunSync()

    def factorial(n: Int): IO[Long] = {

      def doFactorial(n: Int, acc: Long): IO[Long] = {
        IO(n * acc).flatMap { num =>
          if (n > 1)
            doFactorial(n - 1, num)
          else
            IO.pure(acc)
        }
      }
      doFactorial(n, 1)
    }

    val program2 =
      for {
        result <- factorial(5)
        _      <- IO(println(s"Result fib(10)=${result}"))
      } yield ()
    program2.unsafeRunSync()

  }

  example1()
  example2()
  example3()

}
