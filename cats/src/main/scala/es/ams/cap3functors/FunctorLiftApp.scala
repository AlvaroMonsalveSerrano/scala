package es.ams.cap3functors

import cats.Functor
import cats.instances.list._
import cats.instances.option._

object FunctorLiftApp extends App {

  def ejemploFuncionLiftDeList(): Unit = {
    val funcion1 = (x:Int) => x + 1

    val funcionLift = Functor[List].lift(funcion1)

    println(s"Lift de List(1,2)=${funcionLift(List(1,2))}")
    println
  }

  def ejemploFuncionLiftDeOption(): Unit = {
    val funcion1 = (x:Int) => x + 1

    val funcionLift = Functor[Option].lift(funcion1)

    println(s"Lift de Option(1,2)=${funcionLift(Option(1))}")
    println
  }



  ejemploFuncionLiftDeList
  ejemploFuncionLiftDeOption

}
