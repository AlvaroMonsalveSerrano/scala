package es.ams

import org.scalatest._

class HelloSpecRoot extends FlatSpec with Matchers {
  "The Hello object" should "say hello" in {
    Application.greeting shouldEqual "hello"
  }
}
