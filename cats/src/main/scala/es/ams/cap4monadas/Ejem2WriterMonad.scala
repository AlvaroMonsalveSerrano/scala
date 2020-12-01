package es.ams.cap4monadas

import cats.data.Writer
import cats.instances.vector._
import cats.syntax.writer._
import cats.syntax.applicative._

object Ejem2WriterMonad extends App{

  type Logged[A] = Writer[Vector[String], A]



  /**
    * Ejemplos de composición de funciones WriiterMonad
    * For comprenhension de 3 mónadas writer.
    *
    */
  def ejemplo1(): Unit = {

    println(s"--- Ejemplo de for comprenhension con mónada writer ---")
    val writer1 = for {
      a <- 10.pure[Logged]
      _ <- Vector("a","b","c").tell
      b <- 32.writer(Vector("x","y","z"))
    } yield a + b

    val (log, result) = writer1.run
    println(s"1.1 Log=${log}")
    println(s"1.2 Result=${result}")
    println()

  }


  def fWriter1:  Logged[Int]  = {
    val writer1 = for {
      a <- 10.pure[Logged]
      _ <- Vector("a","b","c").tell
      b <- 32.writer(Vector("x","y","z"))
    } yield a + b
    writer1
  }

  /**
    * Ejemplo de transformación de Mónada Writer con mapWriter.
    * Transformamos solo la parte de los mensajes.
    */
  def ejemplo2(): Unit = {

    println(s"--- Ejemplo de transformación de mensajes de mónada writer con función mapWritten---")
    val writer2 = fWriter1.mapWritten( _.map(_.toUpperCase) )
    val (log, result) = writer2.run
    println(s"2.1 Log=${log}")
    println(s"2.2 Result=${result}")
    println()

  }


  /**
    * Ejemplo de transformación de Mónada Writer de la parte de mensajes y resultado.
    * Empleamos la función biMap.
    *
    */
  def ejemplo3(): Unit = {

    println(s"--- Ejemplo de transformación de mensajes y resultado de mónada writer con función bimap---")
    val writer3= fWriter1.bimap(
      log => log.map(_.toUpperCase ),
      result => result * 100
    )
    val (log, result) = writer3.run
    println(s"3.1 Log=${log}")
    println(s"3.2 Result=${result}")
    println()

  }


  /**
    * Ejemplo de transformación de Mónada Writer de la parte de mensajes y resultado.
    * Empleamos la función mapBoth.
    *
    */
  def ejemplo4(): Unit = {

    println(s"--- Ejemplo de transformación de mensajes y resultado de mónada writer con función mapBoth ---")
    val writer4= fWriter1.mapBoth{ (log, result) =>
      val log2 = log.map(_.toUpperCase )
      val result2 = result * 100
      (log2, result2)
    }
    val (log, result) = writer4.run
    println(s"4.1 Log=${log}")
    println(s"4.2 Result=${result}")
    println()

  }

  def ejemplo5(): Unit = {

    println(s"--- Ejemplo de reseteo de mónada writer ---")
    val writer5 = fWriter1
    val (log, result) = writer5.run
    println(s"MÓNADA INICIAL:")
    println(s"5.1 Log=${log}")
    println(s"5.2 Result=${result}")
    println()
    val (log2, result2) = writer5.reset.run
    println(s"MÓNADA POST-RESET:")
    println(s"5.1 Log=${log2}")
    println(s"5.2 Result=${result2}")
    println()

  }


  /**
    * La función swap permite el intercambio del resultado por los logs y, viceversa.
    */
  def ejemplo6(): Unit = {

    println(s"--- Ejemplo de uso de la función swap ---")
    val writer6 = fWriter1.swap
    val (log, result) = writer6.run
    println(s"MÓNADA INICIAL:")
    println(s"6.1 Log=${log}")
    println(s"6.2 Result=${result}")
    println()

  }

  ejemplo1()
  ejemplo2()
  ejemplo3()
  ejemplo4()
  ejemplo5()
  ejemplo6()

}
