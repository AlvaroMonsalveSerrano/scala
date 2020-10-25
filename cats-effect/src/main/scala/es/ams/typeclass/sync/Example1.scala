package es.ams.typeclass.sync

import cats.effect.{IO, Sync}


/**
  * Sync
  * ------
  *
  * https://typelevel.org/cats-effect/typeclasses/sync.html
  *
  */
object Example1 extends App{

  def example1(): Unit = {

    println(s"-*- Example1 -*-")

    val operation1: IO[Unit] = Sync[IO].delay(println("Operation1"))
    val operation2: IO[Int] = Sync[IO].delay( 5 )
    val operation3: IO[Unit] = Sync[IO].delay( println("End program") )

    val program1 = for {
      _ <- operation1
      value <- operation2
      _ <- operation3
    } yield { value }

    val result = program1.unsafeRunSync()
    println(result)

  }

  /**
    * IO(...) = IO.apply(...) = Sync[A].delay(...)
    */
  def example2(): Unit = {

    println(s"-*- Example2 -*-")

    val operation1: IO[Unit] = IO(println("Operation1"))
    val operation2: IO[Int] = IO.apply( 5 )
    val operation3: IO[Unit] = IO( println("End program") )

    val program1 = for {
      _ <- operation1
      value <- operation2
      _ <- operation3
    } yield { value }

    val result = program1.unsafeRunSync()
    println(result)

  }

  example1()
  example2()

}
