package es.ams.cap4monadas

import cats.Monad
import cats.instances.all._

/**
  * Ejemplos básicos de Mónadas
  */
object Ejem1Monada extends App {

  def ejemTypeClassOption(): Unit = {
    println(s"-*- Ejemplos Type Class Monad[Option] -*-\n")
    val opt1 = Monad[Option].pure(3)
    println(s"opt1=${opt1}")
    println()

    val opt11 = Monad[Option].pure(false)
    println(s"opt1-1=${opt11}")
    println()

    val opt2 = Monad[Option].flatMap(opt1)(a => Some(a + 2))
    println(s"opt2=${opt2}")
    println()

    val opt3 = Monad[Option].map(opt2)(a => a * 100)
    println(s"opt3=${opt3}")
    println()
  }

  def ejemTypeClassList(): Unit ={
    println(s"-*- Ejemplos Type Class Monad[List] -*-\n")
    val list1 = Monad[List].pure(3)
    println(s"list1=${list1}")
    println()

    val list2 = Monad[List].flatMap(List(1,2,3))(a => List(a, a * 10) )
    println(s"list2=${list2}")
    println()

    val list3 = Monad[List].map(list2)(a =>  a * 10)
    println(s"list3=${list3}")
    println()
  }

  def ejemTypeClassVector(): Unit = {
    println(s"### Ejemplos Type Class Monad[Vector] ###")
    val vect1 = Monad[Vector].flatMap(Vector(1,2,3))(a => Vector(a, a*10))
    println(s"vect1=${vect1}")
    println()

    val vect2 = Monad[Vector].map(Vector(1,2,3))(a => a*10)
    println(s"vect2=${vect2}")
    println()
  }

  def ejemTypeClassFuture(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent._
    import scala.concurrent.duration._

    println(s"### Ejemplos Type Class Monad[Future] ###")
    val fm = Monad[Future]
    val future = fm.flatMap( fm.pure(9) )(a => Future(a * 10))
    val result1 = Await.result(future, 1.second)
    println(s"result1=${result1}")
    println()

    val future2 = fm.flatMap( fm.pure(7) )(a => fm.pure(a * 10))
    val result2 = Await.result(future2, 1.second)
    println(s"result2=${result2}")
    println()
  }

  ejemTypeClassOption()
  ejemTypeClassList()
  ejemTypeClassVector()
  ejemTypeClassFuture()

}
