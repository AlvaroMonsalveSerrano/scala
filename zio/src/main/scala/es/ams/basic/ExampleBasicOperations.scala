package es.ams.basic

import zio.{IO, Runtime, Schedule, Task, UIO, ZIO}

import java.io.{FileNotFoundException}
import scala.io.{Source, StdIn}
import zio.clock._

/** ZIO
  * https://zio.dev/docs/overview/overview_basic_operations
  */
object ExampleBasicOperations extends App {

  val getStrLn: Task[String] = ZIO.effect(StdIn.readLine())

  def putStrLn(line: String): UIO[Unit] = ZIO.effectTotal(println(line))

  def sqrt(io: UIO[Double]): IO[String, Double] = {
    // Podemos sumergir fallos con ZIO.absolve, es lo contrario a either.
    ZIO.absolve(
      io.map(value =>
        if (value < 0.0) Left("Value must be >= 0.0")
        else Right(Math.sqrt(value))
      )
    )
  }

  def readFile(nameFile: String): UIO[List[String]] = {
    IO.succeed(Source.fromFile(nameFile).getLines().toList)
  }

  def readFileCatchAll(nameFile: String): Task[List[String]] = {
    ZIO(Source.fromFile(nameFile).getLines().toList).catchAll {
      case _ => {
        val uriFile = this.getClass.getClassLoader.getResource("default.data").toURI
        readFile(uriFile.getPath)
      }
    }
  }

  def readFileOrDefault(nameFile: String): Task[List[String]] = {
    ZIO(Source.fromFile(nameFile).getLines().toList).catchSome {
      case _: FileNotFoundException => {
        val uriFile = this.getClass.getClassLoader.getResource("default.data").toURI
        readFile(uriFile.getPath)
      }
    }
  }

  def readFileFallback(nameFile: String): Task[List[String]] = {
    // Se trata un efecto, si éste falla, se prueba el efecto definido en orElse.
    ZIO(Source.fromFile(nameFile).getLines().toList).orElse {
      val uriFile = this.getClass.getClassLoader.getResource("default.data").toURI
      readFile(uriFile.getPath)
    }
  }

  def readFileFold(nameFile: String): Task[List[String]] = {
    // Con foldM tratamos el efecto de forma no pura para el éxito o el fallo.
    ZIO(Source.fromFile(nameFile).getLines().toList).fold(_ => List("OK"), data => data)
  }

  def readFileFoldM(nameFile: String): Task[List[String]] = {
    // Con foldM tratamos el efecto de forma pura para el éxito o el fallo.
    ZIO(Source.fromFile(nameFile).getLines().toList)
      .foldM(_ => ZIO.succeed(List("OK")), data => ZIO.succeed(data))
  }

  def readFileFoldM2(nameFile: String): UIO[List[String]] = {
    // Con foldM tratamos el efecto de forma pura para el éxito o el fallo.
    ZIO(Source.fromFile(nameFile).getLines().toList)
      .foldM(_ => ZIO.succeed(List("OK")), data => ZIO.succeed(data))
  }

  def readFileRetrying(nameFile: String): ZIO[Clock, Throwable, List[String]] = {
    // Si se produce un fallo en un efecto lo reintenta 5 veces más.
    ZIO(Source.fromFile(nameFile).getLines().toList)
      .retry(Schedule.recurs(5))
      .catchAll { case _ =>
        ZIO.succeed(List("OK"))
      }
  }

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

    // *> alias de zipRight
    val zipRight2               = putStrLn("Name Right 2?") *> getStrLn
    val resultZipRight2: String = Runtime.default.unsafeRun(zipRight2)
    println(s"=>${resultZipRight2}")

  }

  exampleChaining()
  exampleForComprenhensions()
  exampleZipping()

}
