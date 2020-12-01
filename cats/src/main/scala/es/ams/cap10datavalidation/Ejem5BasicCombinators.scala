package es.ams.cap10datavalidation

import cats.Semigroup
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.instances.list._
import cats.syntax.all._


object Ejem5BasicCombinators extends App{

  sealed trait Check[E, A]{
    def and(that: Check[E,A]): Check[E,A] = And(this, that)

    def or(that: Check[E,A]): Check[E,A] = Or(this, that)

    def apply(a: A)(implicit s:Semigroup[E]): Validated[E, A] = this match {
      case Pure(func) => func(a)

      case And(left, right) =>
        (left(a), right(a)).mapN( (_ ,_) => a )

      case Or(left, right) =>
        left(a) match {
          case Valid(a) => Valid(a)
          case Invalid(e1) => right(a) match {
            case Valid(a) => Valid(a)
            case Invalid(e2) => Invalid( e1 |+| e2 )
          }
        }
    }

  }

  final case class And[E,A](left: Check[E,A], right: Check[E, A]) extends Check[E, A]
  final case class Or[E,A](left: Check[E,A], right: Check[E, A]) extends Check[E, A]
  final case class Pure[E,A](func: A => Validated[E,A]) extends Check[E, A]


  val a: Check[List[String], Int] = Pure{ v =>
    if(v > 2){ v.valid
    }else{ List("Debe ser > 2").invalid }
  }

  val b: Check[List[String], Int] = Pure { v =>
    if (v < -2){ v.valid
    }else{ List("Debe ser < -2").invalid }
  }

  val c: Check[List[String], Int] = Pure { v =>
    if(v == 2){
     v.valid
    }else{ List("No es el 2!").invalid }

  }

  val check: Check[List[String], Int] = a and b

  val check2: Check[List[String], Int] = a or c




  def ejemplo1(): Unit = {
    println(s"-- Ejemplo5: todo ok --")
    println(s"Verificación 1. check(0)=${check(0)}")
    println(s"Verificación 2. check(5)=${check(5)}")
    println(s"Verificación 3. check(10)=${check(10)}")
    println(s"Verificación 4. check2(2)=${check2(2)}")
    println()

  }

  ejemplo1()

}
