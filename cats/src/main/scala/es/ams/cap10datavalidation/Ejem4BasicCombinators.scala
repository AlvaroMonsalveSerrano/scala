package es.ams.cap10datavalidation

import cats.Semigroup
import cats.data.Validated
import cats.instances.list._
import cats.syntax.all._

object Ejem4BasicCombinators extends App {

  sealed trait Check[E, A] {
    def and(that: Check[E, A]): Check[E, A] = And(this, that)

    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] = this match {
      case Pure(func) => func(a)
      case And(left, right) =>
        (left(a), right(a)).mapN((_, _) => a)
    }
  }

  final case class And[E, A](left: Check[E, A], right: Check[E, A]) extends Check[E, A]
  final case class Pure[E, A](func: A => Validated[E, A])           extends Check[E, A]

  val a: Check[List[String], Int] = Pure { v =>
    if (v > 2) {
      v.valid
    } else {
      List("Debe ser > 2").invalid
    }
  }

  val b: Check[List[String], Int] = Pure { v =>
    if (v < -2) {
      v.valid
    } else {
      List("Debe ser < -2").invalid
    }
  }

  val check: Check[List[String], Int] = a and b

  def ejemplo1(): Unit = {
    println(s"-- Ejemplo3: todo ok --")
    println(s"Verificación 1. check(0)=${check(0)}")
    println(s"Verificación 1. check(5)=${check(5)}")
    println(s"Verificación 1. check(10)=${check(10)}")
    println()

  }

  ejemplo1()

}
