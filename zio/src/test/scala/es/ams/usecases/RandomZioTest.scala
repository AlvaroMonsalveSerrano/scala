package es.ams.usecases

import zio.{Promise, ZIO, console, random, system}
import zio.test._
import zio.test.environment.{TestClock, TestConsole, TestRandom, TestSystem}
import zio.clock.currentTime
import zio.duration._

import java.util.concurrent.TimeUnit

object RandomZioTest extends DefaultRunnableSpec {

  val randomLong = suite("Random Long")(
    testM("Test with Long type. Returns the same values") {
      for {
        _  <- TestRandom.setSeed(27) // Asigna una semilla. Siempre retorna los mismos valores.
        r1 <- random.nextLong
        r2 <- random.nextLong
        r3 <- random.nextLong
        r4 <- random.nextLong
      } yield {
        assert(List(r1, r2, r3, r4))(
          Assertion.equalTo(
            List[Long](
              -4947896108136290151L,
              -5264020926839611059L,
              -9135922664019402287L,
              -9040441768004174507L
            )
          )
        )
      }
    },
    testM("Test with Int type. Returns the same values. A buffer is defined with feedInts") {
      for {
        _  <- TestRandom.feedInts(1, 9, 2, 8, 3, 7, 4, 6, 5) // Mantiene valores internos
        r1 <- random.nextInt
        r2 <- random.nextInt
        r3 <- random.nextInt
        r4 <- random.nextInt
        r5 <- random.nextInt
        r6 <- random.nextInt
        r7 <- random.nextInt
        r8 <- random.nextInt
        r9 <- random.nextInt
      } yield {
        assert(
          List(1, 9, 2, 8, 3, 7, 4, 6, 5)
        )(
          Assertion.equalTo(List(r1, r2, r3, r4, r5, r6, r7, r8, r9))
        )
      }
    }
  )

  val clockTest = suite("Clock")(
    testM("Test with clock. We can move the time...1 minute") {
      for {
        startTime <- currentTime(TimeUnit.SECONDS)
        _         <- TestClock.adjust(1.minute) // Simulamos el paso de 1 minuto.
        endTime   <- currentTime(TimeUnit.SECONDS)
      } yield {
        assert(endTime - startTime)(Assertion.isGreaterThanEqualTo(60L))
      }
    },
    testM("Test with clock and promise.") {
      for {
        promise <- Promise.make[Unit, Int]
        _       <- (ZIO.sleep(10.seconds) *> promise.succeed(1)).fork // Se ejecuta en un fiber independiente.
        _       <- TestClock.adjust(10.seconds) // Simulamos el paso de 10 segundos.
        readRef <- promise.await
      } yield {
        assert(1)(Assertion.equalTo(readRef))
      }

    }
  )

  val consoleTest = suite("Console")(
    testM("Simulate test with TestConsole") {
      for {
        _              <- TestConsole.feedLines("Nat", "25")
        _              <- console.putStrLn("Name?")
        name           <- console.getStrLn
        _              <- console.putStrLn("Age?")
        age            <- console.getStrLn.map(_.toInt)
        questionVector <- TestConsole.output
        q1 = questionVector(0)
        q2 = questionVector(1)
      } yield {
        assert(name)(Assertion.equalTo("Nat")) &&
        assert(age)(Assertion.equalTo(25)) &&
        assert(q1)(Assertion.equalTo("Name?\n")) &&
        assert(q2)(Assertion.equalTo("Age?\n"))
      }
    }
  )

  val testSystem = suite("Test System")(
    testM("Environment JVM property") {
      for {
        _      <- TestSystem.putProperty("java.vm.name", "VMTest")
        result <- system.property("java.vm.name")
      } yield {
        assert(result)(Assertion.equalTo(Some("VMTest")))
      }

    }
  )

  def spec = suite("Random Zio Test")(randomLong, clockTest, consoleTest, testSystem)

}
