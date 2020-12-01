package es.ams.cap5monadtransformer

import cats.data.{OptionT}
import cats.instances.list._
import cats.syntax.applicative._

object Ejem1MonadTransformer extends App {

  /**
    * Elemplo de definición de una Mónada transformer de Lista de Option
    */
  def example1OptionT(): Unit = {

    type ListOption[A] = OptionT[List, A]

    println(s"-*-*- Ejemplo1 de OptionT -*-*-")
    val elem1: ListOption[Int] = 5.pure[ListOption]
    println(s"elem1: ListOption[Int]=${elem1}")
    println()

    val resutlElem1 = for {
      elem <- elem1
    } yield {
      println(s"elem=$elem")
      elem
    }
    println(s"resutlElem1=${resutlElem1}") // OJO, aquí no sacamos nada
    println(s"resutlElem1=${resutlElem1.value}")
    println()

    val elem2: ListOption[Int] = 6.pure[ListOption]
    println(s"elem2: ListOption[Int]=${elem2}")
    println()

    val sumElem1Elem2: ListOption[Int] = for {
      e1 <- elem1
      e2 <- elem2
    } yield {
      println(s"e1=$e1")
      println(s"e2=$e2")
      e1 + e2
    }
    println(s"sumElem1Elem2=${sumElem1Elem2}")
    println()

  }

//  def example2OptionT(): Unit = {
//
//    println(s"-*-*- Ejemplo2 de OptionT -*-*-")
//    type ErrorOr[A] = Either[String, A]
//    type ErrorOrOption[A] = OptionT[ErrorOr, A]
//
//    val elem1: ErrorOrOption[Int] = 5.pure[ErrorOrOption]
//    println(s"elem1: ListOption[Int]=${elem1}")
//    println(s"elem1.value: ListOption[Int]=${elem1.value}")
//    println
//
//    val elem2: ErrorOrOption[Int] = 6.pure[ErrorOrOption]
//    println(s"elem2: ListOption[Int]=${elem2}")
//    println(s"elem2.value: ListOption[Int]=${elem2.value}")
//    println
//
//    // No entiendo porqué el IDE me muestra error de implícitos para el Functor y Mónada de ErrorOn
//    val sumElem1Elem2: ErrorOrOption[Int] =
//      elem1.flatMap(e1 =>
//        elem2.map(e2 => e1 + e2))  // A nivel de compilación da fallo pero funciona bien al ejecutar.
//    println(s"sumElem1Elem2: ListOption[Int]=${sumElem1Elem2}")
//    println
//
//  }

    example1OptionT()
//    example2OptionT()

}
