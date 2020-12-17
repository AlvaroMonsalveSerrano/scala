package es.ams.usecases

import zio.test.Assertion.{equalTo, isTrue}
import zio.test.{DefaultRunnableSpec, Gen, Sized, assert, check, suite, testM}
import zio.random.Random
import zio.test.magnolia._

object PropertyZioTest extends DefaultRunnableSpec {

  final case class Point(x: Double, y: Double) {
    def isValid(): Boolean = true
  }
  val genPoint: Gen[Random with Sized, Point] = DeriveGen[Point]

  sealed trait Color {
    def isValid(): Boolean = true
  }
  case object Red   extends Color
  case object Green extends Color
  case object Blue  extends Color
  val genColor: Gen[Random with Sized, Color] = DeriveGen[Color]

  def spec = suite("Property Testing")(
    testM("Gen Int") {
      check(Gen.anyInt, Gen.anyInt, Gen.anyInt) { (x, y, z) =>
        assert((x + y) + z)(equalTo(x + (y + z)))
      }
    },
    testM("Gen Point") {
      check(genPoint) { (point) =>
        assert(point.isValid())(equalTo(true))
      }
    },
    testM("Gen Color") {
      check(genColor) { (color: Color) =>
        assert(color.isValid())(isTrue)
      }
    }
  )

}
