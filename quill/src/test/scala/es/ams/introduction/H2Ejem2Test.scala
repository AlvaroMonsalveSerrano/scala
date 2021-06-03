package es.ams.introduction

import io.getquill._

class H2Ejem2Test extends munit.FunSuite {

  val ctx = new H2JdbcContext(SnakeCase, "ctx")
  import ctx._

  case class Circle(radius: Float, cname: String)

  val pi = quote(3.14159)

  def insertCicle(circle: Circle) = quote {
    query[Circle]
      .insert(
        lift(circle)
      )
      .returningGenerated(_.cname)
  }

  def biggerThan(i: Float) = quote {
    query[Circle].filter(r => r.radius > lift(i))
  }

  def selectCicleAll() = quote {
    query[Circle]
  }

  def selectINList(radiusList: List[Long]) = quote {
    query[Circle].filter(r => liftQuery(radiusList).contains(r.radius))
  }

  val selectOneRadiusFirstClass = quote {
    query[Circle].filter(c => c.radius == 1)
  }

  def insertDataTest() = {
    ctx.run(insertCicle(Circle(radius = 1, cname = "circle_1")))
    ctx.run(insertCicle(Circle(radius = 2, cname = "circle_2")))
    ctx.run(insertCicle(Circle(radius = 3, cname = "circle_3")))
    ctx.run(insertCicle(Circle(radius = 4, cname = "circle_4")))
  }

  insertDataTest()

  test("Circle, select all") {
    val result1 = ctx.run(selectCicleAll())
    assertEquals(result1.size, 4)
  }

  test("Circle, select filter radius >") {
    val result2 = ctx.run(biggerThan(2))
    assertEquals(result2.size, 2)
  }

  test("Circle: select .. in ...") {
    val result3 = ctx.run(selectINList(List(3, 4)))
    assertEquals(result3.size, 2)
  }

  test("Circle: defien función in firsClass") {
    val result = ctx.run(selectOneRadiusFirstClass)
    assertEquals(result.size, 1)
  }

}
