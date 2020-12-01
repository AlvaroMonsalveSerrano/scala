package es.ams.cap4monadas

import cats.Monad
import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.instances.all._


/**
  * Ejemplo de mónada: primero, con flatMap y map; y, segundo, con for comprehension
  *
  * Ejemplo de uso de la mónada identidad
  *
  */
object Ejem2Monada extends App {


  def sumaMonadica[F[_]: Monad]( e1:F[Int], e2:F[Int], e3:F[Int] ): F[Int] = {
    e1.flatMap( a =>
      e2.flatMap( b =>
        e3.map( c =>
          a + b + c ) ))
  }

  def sumaMonadicaFor[F[_]: Monad]( e1:F[Int], e2:F[Int], e3:F[Int] ): F[Int] = {
    for{
      a <- e1
      b <- e2
      c <- e3
    }yield{
      a + b + c
    }
  }


  def monadaFlatMapFor(): Unit = {
    val monadaOption = Monad[Option]
    println(s"sumaMonadica(Some(2), Some(3), Some(4))=${ sumaMonadica(monadaOption.pure(2), monadaOption.pure(3), monadaOption.pure(4)) }")
    println()
    println(s"sumaMonadicaFor(Some(2), Some(3), Some(4))=${ sumaMonadicaFor(monadaOption.pure(2), monadaOption.pure(3), monadaOption.pure(4)) }")
    println()
    // Si ponemos todo con Some no funciona.
    println(s"sumaMonadicaFor(Some(2), Some(3), Some(4))=${ sumaMonadicaFor(Option(2), Option(3), Option(4)) }")
    println()
  }


  def monadaFlatMapForId(): Unit = {
    import cats.Id // type Id[A] = A
    val monadaId = Monad[Id]
    println(s"sumaMonadicaId(2, 3, 4)=${ sumaMonadica(monadaId.pure(2), monadaId.pure(3), monadaId.pure(4))}")
    println()
    println(s"sumaMonadicaFor(2, 3, 4)=${ sumaMonadicaFor(monadaId.pure(2), monadaId.pure(3), monadaId.pure(4)) }")
    println()
  }

  monadaFlatMapFor()
  monadaFlatMapForId()


}
