package es.ams.basic

import zio.Exit.Success
import zio.clock.Clock
import zio.{IO, Runtime, ZIO}

class BasicConcurrencyTest extends BaseClassTest {

  "ZIO basic concurrency" should "forking effects" in {

    val forFiber = for {
      fiber <- IO.succeed("End")
    } yield (fiber)
    val resultOperation = Runtime.default.unsafeRun(forFiber)
    assertResult("End")(resultOperation)

  }

  it should "Joining Fibers" in {

    /** join. Devuelve un efecto. El efecto devuelto tendrá éxito o fallará según la fibra.
      */
    val joinFiber = for {
      fiber   <- IO.succeed("Hi!").fork
      message <- fiber.join
    } yield (message)
    val resultOperation = Runtime.default.unsafeRun(joinFiber)
    assertResult("Hi!")(resultOperation)

  }

  it should "Awaiting fibers" in {

    /** await, devuelve un efecto que contiene un Exit, proporciona información sobre cómo se completo la fibra.
      */
    val awaitFiber = for {
      fiber <- IO.succeed("Hi!").fork
      exit  <- fiber.await
    } yield (exit)

    val resultOperation = Runtime.default.unsafeRun(awaitFiber)
    assertResult(Success("Hi!"))(resultOperation)

  }

  it should "Interrupting Fibers" in {

    /** Interrupt. Fiber que no se necesita. Se termina de ejecutar, se retorna el resultado y se liberan los recursos.
      */
    val interruptFiber = for {
      fiber <- IO.succeed("Hi Interrupt!").forever.fork
      exit  <- fiber.interrupt
    } yield (exit)

    val resultOperation = Runtime.default.unsafeRun(interruptFiber)
    assertResult(true)(resultOperation.interrupted)

  }

  it should "Composing Fiber" in {

    /** Composición de dos fibras en una única. Si una de las fibras falla, la composición falla.
      */
    val composingFiber1 = for {
      fiber1 <- IO.succeed("Hi!").fork
      fiber2 <- IO.succeed("bye!").fork
      fiber = fiber1.zip(fiber2)
      tuple <- fiber.join
    } yield (tuple)

    val resultOperation1 = Runtime.default.unsafeRun(composingFiber1)
    assertResult(("Hi!", "bye!"))(resultOperation1)

    val composingFiber2 = for {
      fiber1 <- IO.fail("Hi!").fork
      fiber2 <- IO.succeed("bye!").fork
      fiber = fiber1.zip(fiber2)
      tuple <- fiber.join
    } yield (tuple)

    val resultOperation2 = Runtime.default.unsafeRun(composingFiber2.catchAll { case _ =>
      IO("Fail Error")
    })
    assertResult(("Fail Error"))(resultOperation2)

  }

  it should "Racing" in {

    /** Zio permite correr multiples efectos en paralelo  retornar aquel que termina primero.
      */
    val raceFiber = for {
      winner <- IO.succeed("Hello").race(IO.succeed("Bye"))
    } yield (winner)

    val resultOperation = Runtime.default.unsafeRun(raceFiber)
    assert(resultOperation.length > 0)

  }

  it should "Timeout" in {

    /** Timeout. Permite desconectar un efecto en un timeout. Retorna el fecto en un Option, None si se ha pasado el
      * tiempo.
      */
    import zio.duration._

    val timeoutFiber: ZIO[Clock, Nothing, Option[String]] = for {
      fiber <- IO.succeed("Hello").timeout(10.seconds)
    } yield (fiber)

    val resultOperation: Option[String] = Runtime.default.unsafeRun(timeoutFiber)
    println(s"->${resultOperation.isInstanceOf[Option[String]]}")

  }

}
