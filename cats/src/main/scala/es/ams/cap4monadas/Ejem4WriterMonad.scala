package es.ams.cap4monadas

import cats.data.Writer
import cats.syntax.applicative._
import cats.syntax.writer._
import cats.instances.vector._

/** Ejemplo de una aplicación de Mónada Writer.
  *
  * Ejemplos para la entrada del blog.
  */
object Ejem4WriterMonad extends App {

  def ejemplo1(): Unit = {
    type Logged[A] = Writer[Vector[String], A]

    /** Función que realiza la suma de los números pares desde el número 0 hasta el número N.
      *
      * @param n Int
      * @param f (Int => Int)
      *
      * @return Logged[Int]
      */
    def sumaPares(n: Int)(f: (Int => Int)): Logged[Int] = {
      val result = for {
        acc <-
          if (n == 0) {
            0.pure[Logged]
          } else {
            sumaPares(n - 1)(f).map(_ + f(n))
          }
        _ <- Vector(s"Suma Pares $n=$acc").tell
      } yield {
        acc
      }
      result
    }

    val esPar: (Int => Int) = (n: Int) => if (n % 2 == 0) n else 0

    val (log, result) = sumaPares(5)(esPar).run
    println(s"1.1 Log=${log}")
    println(s"1.2 Result=${result}")
    println()

  }

  /** Ejemplo que realiza la creación de una lista de enteros a partir de una lista de entrada.
    */
  def ejemplo2(): Unit = {

    type Logged[A] = Writer[Vector[String], A]

    /** Función que realiza la creación de un String de los números pares de una lista.
      * @param lista
      * @param f
      * @return
      */
    def listaEnteros(lista: List[Int])(f: (Int => String)): Logged[String] = {
      val result = for {
        acc <- lista match {
          case Nil          => "".pure[Logged]
          case head :: tail => listaEnteros(tail)(f).map(_ + f(head))
        }
        _ <- Vector(s"Lista entera de $lista='$acc'").tell
      } yield {
        acc
      }
      result
    }

    val esPar: (Int => String) = (n: Int) => if (n % 2 == 0) n.toString else ""
    val lista                  = (1 to 5).toList
    val (log, result)          = listaEnteros(lista)(esPar).run
    println(s"2.1 Log=${log}")
    println(s"2.2 Result=${result.split("").toList}") // Creación de la lista.
    println()

  }

  ejemplo1()
  ejemplo2()

}
