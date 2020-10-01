package es.ams.cap10datavalidation

import cats.Semigroup
import cats.instances.list._
import cats.syntax.either._
import cats.syntax.semigroup._

/**
  * Segunda aproximación de definición de un Check utilizando un ADT.
  *
  * Esta opción es más "verbosa" pero es más limpia por la separación entre la estructura y la computación.
  *
  */
object Ejem3BasicCombinators extends App{

  sealed trait Check[E, A] {
    def and(that: Check[E,A]): Check[E,A] = And(this, that)

    def apply(a: A)(implicit s: Semigroup[E]): Either[E, A] = this match {
      case Pure(func) => func(a)

      case And(left, right) =>
        (left(a), right(a)) match {
          case (Left(e1), Left(e2)) => (e1 |+| e1).asLeft
          case (Left(e), Right(a)) => e.asLeft
          case (Right(a), Left(e)) => e.asLeft
          case (Right(a1), Right(a2)) => a.asRight
        }
    }
  }

  final case class Pure[E, A](func: A => Either[E, A]) extends Check[E, A]

  final case class And[E, A](left: Check[E,A], right: Check[E, A]) extends Check[E, A]


  val a: Check[List[String], Int] = Pure{ v =>
    if(v > 2){ v.asRight
    }else{ List("Debe ser > 2").asLeft }
  }

  val b: Check[List[String], Int] = Pure{ v =>
    if(v < -2){ v.asRight
    }else{ List("Debe ser < -2").asLeft }
  }

  val check: Check[List[String], Int] = a and b

  def ejemplo1(): Unit = {
    println(s"-- Ejemplo3: todo ok --")
    println(s"Verificación 1. check(0)=${check(0)}")
    println(s"Verificación 1. check(5)=${check(5)}")
    println(s"Verificación 1. check(10)=${check(10)}")
    println
  }

  ejemplo1()

}
