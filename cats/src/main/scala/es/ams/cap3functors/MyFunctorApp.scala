package es.ams.cap3functors

import cats.instances.all._
import es.ams.cap3functors.MyFunctor.syntax._


/**
  * Aplicación de ejemplo del functor  es.ams.cap3functors.MyFunctor._
  * Se utiliza la sintaxis para las pruebas.
  *
  */
object MyFunctorApp extends App {

  def transformadorList(): Unit = {
    val lista = List(1,2,3,4)
    val funcion1 = (x: Int) => x + 1

    val resultado1 = lista.transformador(funcion1)
    println(s"Transformador de List=${resultado1}")
    println
  }

  def transformadorOption(): Unit = {
    val option = Option(10)
    val funcion1 = (x: Int) => x + 1

    val resultado1 = option.transformador(funcion1)
    println(s"Transformador de Option=${resultado1}")
    println
  }

  transformadorList
  transformadorOption

}
