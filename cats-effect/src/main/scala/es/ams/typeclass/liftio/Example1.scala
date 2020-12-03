package es.ams.typeclass.liftio

import cats.data.EitherT
import cats.effect.{IO, LiftIO}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/** LiftIO
  * ------
  *
  * https://typelevel.org/cats-effect/typeclasses/liftio.html
  */
object Example1 extends App {

  type MyEffect[A] = Future[Either[Throwable, A]]

  // Defines the LiftIO instance
  implicit def myEffectLiftIO: LiftIO[MyEffect] = new LiftIO[MyEffect] {
    override def liftIO[A](ioa: IO[A]): MyEffect[A] =
      ioa.attempt.unsafeToFuture()
  }

  /** Example of the cats effects documentation.
    */
  def example1(): Unit = {

    println(s"-*- Example1 -*-")
    val ioa: IO[String]          = IO("Hello World!!")
    val effect: MyEffect[String] = LiftIO[MyEffect].liftIO(ioa)

    effect.onComplete {
      case Success(value)     => println(s"Result example1=${value}")
      case Failure(exception) => println(s"Exception example1=${exception}")
    }

    // Esperamos a la terminacion del Future.
    Thread.sleep(3000)
  }

  def example2(): Unit = {

    println(s"-*- Example2 -*-")
    val L = implicitly[LiftIO[MyEffect]]

    val service1: MyEffect[Int]       = Future.successful(Right(22))
    val service2: MyEffect[Boolean]   = Future.successful(Right(true))
    val service2_1: MyEffect[Boolean] = Future.successful(Right(false))
    val service3: MyEffect[String]    = Future.successful(Left(new Exception("boom!")))

    val program: MyEffect[String] =
      (for {
        _ <- EitherT(service1)
        x <- EitherT(service2)
        y <- EitherT(
          if (x)
            L.liftIO(IO("from io"))
          else
            service3
        )
      } yield y).value

    program.onComplete {
      case Success(value)     => println(s"value=${value}")
      case Failure(exception) => println(s"exception=${exception}")
    }

    // Esperamos a la terminacion del Future.
    Thread.sleep(3000)

    val program1: MyEffect[String] =
      (for {
        _ <- EitherT(service1)
        x <- EitherT(service2_1)
        y <- EitherT(
          if (x)
            L.liftIO(IO("from io"))
          else
            service3
        )
      } yield y).value

    program1.onComplete {
      case Success(value)     => println(s"value=${value}")
      case Failure(exception) => println(s"exception=${exception}")
    }

    // Esperamos a la terminacion del Future.
    Thread.sleep(3000)
  }

  example1()
  example2()

}
