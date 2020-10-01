package es.ams.cap10datavalidation

import cats.Semigroup
import cats.instances.list._
import cats.syntax.either._
import cats.syntax.semigroup._

/**
  * Primera aproximación para realizar verificaciones.
  *
  * Esta opción no es del todo buena porque para el cado de CheckF[E, A] con E=Nothing no podemos realizar
  * acumulación porque no tiene instancias del valos implícito de Nothing.
  *
  */
object Ejem2BasicCombinators extends App {


  /**
    * Estritegia 1 para realizar una verificación.
    *
    * @param func
    * @tparam E
    * @tparam A
    */
  final case class CheckF[E, A](func: A => Either[E,A]){
    def apply(a: A): Either[E, A] = func(a)

    def and(that: CheckF[E,A])(implicit s: Semigroup[E]): CheckF[E, A] =
      CheckF{ a =>
        (this(a), that(a)) match {
            case (Left(e1), Left(e2)) => ( e1 |+| e2 ).asLeft // Así, podemos acumular errores.
            case (Left(e), Right(a)) => e.asLeft
            case (Right(a), Left(e)) => e.asLeft
            case (Right(a1), Right(a2)) => a.asRight
        }
      }
  }

  // Definición de una regla de tipo CheckF.
  val a: CheckF[List[String], Int] =
    CheckF{ v =>
      if(v>2){ v.asRight
      }else{ List("Debe de ser > 2").asLeft }
    }

  // Definición de una regla de tipo CheckF
  val b: CheckF[List[String], Int] =
    CheckF{ v =>
      if(v < -2){ v.asRight
      }else{ List("Debe de ser < 2").asLeft }
    }

  // Definición de una regla compuesta por 2.
  val check: CheckF[List[String], Int] = a and b


  def ejemplo1(): Unit = {
    println(s"-- Ejemplo2: todo ok --")
    println(s"Verificación 1. check(0)=${check(0)}")
    println(s"Verificación 1. check(5)=${check(5)}")
    println(s"Verificación 1. check(10)=${check(10)}")
    println
  }


  def ejemplo2(): Unit = {

    val aNothing: CheckF[Nothing, Int] = CheckF(v => v.asRight)

    val bNothing: CheckF[Nothing, Int] = CheckF(v => v.asRight)

    // No puede utilizarse porque no hay un valor implícito para Nothing.
    // No nos permite acumular los valores.
//    val checkABNothint: CheckF[Nothing, Int] = aNothing and bNothing

    println(s"-- Ejemplo2: todo ok --")
    println(s"Verificación 1. aNothing(0)=${aNothing(0)}")
    println(s"Verificación 1. aNothing(5)=${aNothing(5)}")
    println(s"Verificación 1. aNothing(10)=${aNothing(10)}")
    println(s"Verificación 1. bNothing(0)=${bNothing(0)}")
    println(s"Verificación 1. bNothing(5)=${bNothing(5)}")
    println(s"Verificación 1. bNothing(10)=${bNothing(10)}")

    println
  }


  ejemplo1()
  ejemplo2()

}
