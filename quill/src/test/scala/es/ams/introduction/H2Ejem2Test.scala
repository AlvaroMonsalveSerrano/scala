package es.ams.introduction

import io.getquill._
import org.scalatest._
import org.scalatest.flatspec.AnyFlatSpec

class H2Ejem2Test extends AnyFlatSpec with BeforeAndAfter {

  val ctx = new H2JdbcContext(SnakeCase, "ctx")
  import ctx._

  before {
    insertDataTest()
  }

  after {
    deleteDataTest()
  }

  case class Circle(radius: Float, cname: String)

  // ----

  val pi = quote(3.14159)

  def insertCicle(circle: Circle) = quote {
    query[Circle]
      .insert(
        lift(circle)
      )
      .returningGenerated(_.cname)
  }

  def deleteCircle(id: Int) = quote {
    query[Circle].filter(_.radius == lift(id)).delete
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

  def deleteDataTest() = {
    ctx.run(deleteCircle(1))
    ctx.run(deleteCircle(2))
    ctx.run(deleteCircle(3))
    ctx.run(deleteCircle(4))
  }

  "Testing basic operations" should
    "Circle entity. select all" in {
      val result1 = ctx.run(selectCicleAll())
      assert(result1.size > 0)
    }

  it should "Circle, select filter radius >" in {
    val result2 = ctx.run(biggerThan(2))
    assert(result2.size == 2)
  }

  it should "Circle: select .. in ..." in {
    val result3 = ctx.run(selectINList(List(3, 4)))
    assert(result3.size == 2)
  }

  it should "Circle: defien función in firsClass" in {
    val result = ctx.run(selectOneRadiusFirstClass)
    assert(result.size == 1)
  }

}
