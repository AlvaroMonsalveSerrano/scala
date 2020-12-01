package es.ams

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.flatspec.AnyFlatSpec

class HelloSpecRoot extends AnyFlatSpec with Matchers {
  "The Hello object" should "say hello" in {
    Application.greeting shouldEqual "hello"
  }
}
