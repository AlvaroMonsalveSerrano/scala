package es.ams.basic

import io.getquill._

class BasicEjem1Test extends munit.FunSuite {

  val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)
  import ctx._

  case class Circle(radius: Float)
  val pi = quote(3.14159)
  val areas = quote {
    query[Circle].map(c => pi * c.radius * c.radius)
  }

  test("SqlMirrorContext") {

    println(s"areas=${areas}")
    println(s"areas=${ctx.run(areas)}")
    println(s"==>>${ctx.run(query[Circle].map(_.radius))}")

  }

}
