package es.ams.typeclass.async

import cats.effect.{Async, IO}

import scala.concurrent.Future
import scala.util.{Failure, Success}

import scala.concurrent.ExecutionContext.Implicits.global

object Example1 extends App {

  def example1(): Unit = {

    val apiCall1 = Future.successful("Result future operation1")

    // cb = Either[Throwable, A] = Unit
    val operation1: IO[String] = Async[IO].async { cb =>
      apiCall1.onComplete {
        case Success(value)     => cb(Right(value))
        case Failure(exception) => cb(Left(exception))
      }
    }

    val result1 = operation1.unsafeRunSync()
    println(s"Result1=${result1}")
    println()

    val program2 = for {
      _     <- IO(println("Run Async operation"))
      value <- operation1
      _     <- IO(println("End async operation"))

    } yield { value }

    val result2 = program2.unsafeRunSync()
    println(s"Result2=${result2}")
    println()

  }

  example1()

}
