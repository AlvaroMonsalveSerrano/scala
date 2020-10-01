package es.ams.cap10datavalidation

import cats.Semigroup
import cats.instances.list._
import cats.syntax.semigroup._

object Ejem1BasicCombinators extends App{

  /**
    * Ejemplo de creación de agrupaciones de resultados. Se utiliza Monoides y Semigrupos.
    *
    */
  def ejemplo1(): Unit = {
    val semigroup = Semigroup[List[String]]

    val ejem1 = semigroup.combine( List("Ejemplo1 Monoid"), List("Ejemplo2 Monoid") )
    println(s"Resultado ejem1 con Monoid=${ejem1}")
    println


    val ejem2 = List("Ejemplo1 semigroup") |+| List("Ejemplo2 semigroup")
    println(s"Resultado ejem2 con Semigroup=${ejem2}")
    println
  }

  ejemplo1()

}
