package es.ams.usecases

import zio.test._
import zio.clock.nanoTime
import Assertion._

import zio.test.DefaultRunnableSpec

/** Template to do test with ZIO
  */
object TemplateZioTest extends DefaultRunnableSpec {

  val suite1 = suite("suite1")(
    testM("s1.t1") { assertM(nanoTime)(isGreaterThanEqualTo(0L)) },
    testM("s1.t2") { assertM(nanoTime)(isGreaterThanEqualTo(0L)) }
  )

  val suite2 = suite("suite2")(
    testM("s2.t1") { assertM(nanoTime)(isGreaterThanEqualTo(0L)) },
    testM("s2.t2") { assertM(nanoTime)(isGreaterThanEqualTo(0L)) },
    testM("s2.t3") { assertM(nanoTime)(isGreaterThanEqualTo(0L)) }
  )

  val suite3 = suite("suite3")(
    testM("s3.t1") { assertM(nanoTime)(isGreaterThanEqualTo(0L)) }
  )

  def spec = suite("All test")(suite1, suite2, suite3)

}
