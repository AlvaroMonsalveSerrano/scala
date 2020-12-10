package es.ams.basic

import org.scalatest.flatspec.AnyFlatSpec
import zio.{IO, Runtime, Task, ZIO}

class CreatingEffetsTest extends AnyFlatSpec {

  "Creating Effect" should "From success values" in {

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

    val zoption: IO[Option[Nothing], Int] = ZIO.fromOption(Some(2))
    val resultZOption: Int                = Runtime.default.unsafeRun(zoption)
    assert(2 === resultZOption)

    val zoption2: IO[String, Int] = zoption.mapError(_ => "It wasn't there!")
    val resultZOption2: Int       = Runtime.default.unsafeRun(zoption2)
    assert(2 === resultZOption2)

  }

}
