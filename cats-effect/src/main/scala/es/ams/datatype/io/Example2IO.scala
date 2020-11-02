package es.ams.datatype.io

import cats.effect.IO

/**
  * Type class IO
  * -------------
  *
  * Ref: https://typelevel.org/cats-effect/datatypes/io.html
  *
  * Describing Effects
  *
  */
object Example2IO extends App {

  /**
   * Pure Values — IO.pure & IO.unit
    *
    * IO.unit es un alias de IO.pure(())
   */
  def example1(): Unit = {

    println(s"-*- Example1 -*-")
    val program1 = IO.pure(25).flatMap( n => IO(println(s"Number: ${n}")) )
    program1.unsafeRunSync()

    // It is wrong
    // IO.pure(println(s"Number: ${n}"))

  }

  /**
    * Synchronous Effects — IO.apply
    *
    * Equivalente a Sync[IO].delay
    *
    * def apply[A](body: => A): IO[A] = ???
    */
  def example2(): Unit = {

    println("-*- Example 2 -*-")
    def printLine(value: String): IO[Unit] = IO(println(value))
    def readLine(): IO[String] =  IO( scala.io.StdIn.readLine())

    val program1 = for{
      _ <- printLine("insert value:")
      n <- readLine()
      _ <- printLine(s"Value=${n}")

    }yield ()

    program1.unsafeRunSync()

  }

  example1()
  example2()

}
