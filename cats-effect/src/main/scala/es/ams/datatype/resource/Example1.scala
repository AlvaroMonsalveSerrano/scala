package es.ams.datatype.resource

import cats.effect.{IO, Resource}

object Example1 extends App {

  def example1(): Unit = {
    println(s"-*- Example1 -*-")

    val greet: String => IO[Unit] =
      x => IO(println(s"Hello ${x}"))

    Resource
      .liftF(IO.pure("World"))
      .use(greet)
      .unsafeRunSync()

  }

  /** Definición de un recurso
    */
  def example2(): Unit = {
    println(s"-*- Example2 -*-")

    val acquire: IO[String] =
      IO(println(s"Acquire cats...")) *> IO("cats")

    val release: String => IO[Unit] =
      _ => IO(println(s"...release everything"))

    val addDogs: String => IO[String] =
      x => IO(println(s"...more animals...")) *> IO.pure(s"${x} and dogs")

    val report: String => IO[String] =
      x => IO(println(s"...produce weather report...")) *> IO(s"It's raining ${x}")

    Resource
      .make(acquire)(release)
      .evalMap(addDogs)
      .use(report)
      .unsafeRunSync()

  }

  /** Definición de dos recursos y su utilización conjunta.
    */
  def example3(): Unit = {
    println(s"-*- Example3 -*-")

    def mkResource(s: String) = {
      val acquire = IO(println(s"Acquiring $s")) *> IO.pure(s)

      def release(s: String) = IO(println(s"Releasing $s"))

      Resource.make(acquire)(release)
    }

    val result = for {
      outer <- mkResource("outer")
      inner <- mkResource("inner")
    } yield (outer, inner)

    result
      .use { case (a, b) =>
        IO(println(s"Using $a and $b"))
      }
      .unsafeRunSync()

  }

  /** Definición de un Resource "AutoCloseable"
    */
  def example4(): Unit = {
    println(s"-*- Example4 -*-")

    val acquire = IO {
      scala.io.Source.fromString("Hello World")
    }

    Resource
      .fromAutoCloseable(acquire)
      .use(source => IO(println(source.mkString)))
      .unsafeRunSync()

  }

  example1()
  example2()
  example3()
  example4()

}
