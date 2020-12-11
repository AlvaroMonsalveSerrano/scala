package es.ams.basic

import zio.{Runtime, Task, UIO, ZIO}

import scala.io.StdIn

/** ZIO
  * https://zio.dev/docs/overview/overview_basic_operations
  */
object ExampleCreatingEffects extends App {

  val getStrLn: Task[String] = ZIO.effect(StdIn.readLine())

  def putStrLn(line: String): UIO[Unit] = ZIO.effectTotal(println(line))

  def exampleChaining(): Unit = {

    println(s"-*- Example Chaining -*-")
    val operation1 = getStrLn.flatMap(input => putStrLn(s"-->${input}"))

    Runtime.default.unsafeRun(operation1)

  }

  def exampleForComprenhensions(): Unit = {

    println(s"-*- For Comprehension -*-")
    val program = for {
      _    <- putStrLn("Nombre")
      name <- getStrLn
      _    <- putStrLn(s"Value=${name}")
    } yield ()

    Runtime.default.unsafeRun(program)

  }

  def exampleZipping(): Unit = {

    println(s"-*- Example Zipping -*-")
    val zipRight1               = putStrLn("Name Right 1?").zipRight(getStrLn)
    val resultZipRight1: String = Runtime.default.unsafeRun(zipRight1)
    println(s"=>${resultZipRight1}")

    val zipRight2               = putStrLn("Name Right 2?") *> getStrLn
    val resultZipRight2: String = Runtime.default.unsafeRun(zipRight2)
    println(s"=>${resultZipRight2}")

  }

  exampleChaining()
  exampleForComprenhensions()
  exampleZipping()

}
