package es.ams.basic

import org.scalatest.flatspec.AnyFlatSpec
import zio.{IO, Runtime, Task, UIO, ZIO}

import scala.concurrent.Future
import scala.util.Try

class CreatingEffetsTest extends AnyFlatSpec {

  "ZIO Creating Effect" should "From success values" in {

    val int42 = for {
      intS1 <- ZIO.succeed(42)
    } yield (intS1)

    val resultInt42 = Runtime.default.unsafeRun(int42)
    assert(42 === resultInt42)

    val task42: Task[Int] = Task.succeed(42)
    val resultTask42: Int = Runtime.default.unsafeRun(task42)
    assert(42 === resultTask42)

    val effectTotal: Task[Long] = ZIO.effectTotal(System.currentTimeMillis())
    val resultEffectTotal: Long = Runtime.default.unsafeRun(effectTotal)
    assert(resultEffectTotal > 0)

  }

  it should "From failure values" in {
    // TODO
  }

  it should "From scala values" in {

    // Option
    val zoption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(2))
    val resultZOption: Int                = Runtime.default.unsafeRun(zoption)
    assert(2 === resultZOption)

    val zoption2: IO[String, Int] = zoption.mapError(_ => "It wasn't there!")
    val resultZOption2: Int       = Runtime.default.unsafeRun(zoption2)
    assert(2 === resultZOption2)

    // Either
    val zeither: IO[Either[Exception, String], String] = ZIO.fromEither(Right("Success"))
    val resultZeither: String                          = Runtime.default.unsafeRun(zeither)
    assert("Success" === resultZeither)

    // Try
    val ztry: Task[Int] = ZIO.fromTry(Try(40 / 2))
    val resultZTry: Int = Runtime.default.unsafeRun(ztry)
    assert(20 === resultZTry)

    // Function
    // TODO Determinar cómo se ejecuta.
    // val zfun: URIO[Int, Int] = ZIO.fromFunction((i: Int) => i * i)

    // Future
    lazy val future = Future.successful("Hi!")

    val zfuture: Task[String] = ZIO.fromFuture { implicit ec =>
      future.map(_ => "Goodbye!")
    }
    val resultZFuture: String = Runtime.default.unsafeRun(zfuture)
    assert("Goodbye!" === resultZFuture)

  }

  it should "From Side-Effects" in {

    // Synchronous Side-Effects
    // val getStrLn: Task[String] = ZIO.effect(StdIn.readLine())

    def putStrLn(line: String): UIO[Unit] =
      ZIO.effectTotal(println(line))

    val resultPut: Unit = Runtime.default.unsafeRun(putStrLn("Test"))
    assert(resultPut === ())

  }

}
