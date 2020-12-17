package es.ams.usecases

import zio.Semaphore
import zio.IO
import zio.test.Assertion.{equalTo, hasField, isGreaterThanEqualTo, isRight, isSome, not}
import zio.test._
import zio.test.DefaultRunnableSpec

object TestEffectsZioTest extends DefaultRunnableSpec {

  def spec = suite("TestEffects")(
    testM("Assertion examples: string") {
      for {
        word <- IO.succeed("The StringTest")
      } yield {
        assert(word)(
          Assertion.containsString("StringTest") &&
            Assertion.endsWithString("Test")
        )
      }
    },
    testM("Assertion examples: either") {
      for {
        either <- IO.succeed(Right(Some(2)))
      } yield {
        assert(either)(isRight(isSome(equalTo(2))))
      }
    },
    testM("Assertion examples: case class") {
      final case class Address(country: String, city: String)
      final case class User(name: String, age: Int, address: Address)

      for {
        test <- IO.succeed(User("Nat", 25, Address("France", "Paris")))
      } yield {
        assert(test)(
          hasField("age", (u: User) => u.age, isGreaterThanEqualTo(18)) &&
            hasField("country", (u: User) => u.address.country, not(equalTo("USA")))
        )
      }
    },
    testM("Assertion examples: semaphore") {
      for {
        semaphore <- Semaphore.make(1L)
        permits   <- semaphore.available
      } yield {
        assert(permits)(Assertion.equalTo(1L))
      }

    }
  )

}
