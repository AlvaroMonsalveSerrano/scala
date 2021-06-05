package es.ams.introduction

import io.getquill.{H2JdbcContext, SnakeCase}
import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec

class BaseTest extends AnyFlatSpec with BeforeAndAfter {

  protected val ctx = new H2JdbcContext(SnakeCase, "ctx")
  import ctx._

  case class CircleInfo(id: Int, info1: String, info2: String, circle_id: Int)
  case class Circle(id: Int, radius: Float, cname: String)

  //  Schema customization
  object schema {

    val circles = quote {
      querySchema[Circle](
        "Circle",
        _.id     -> "id",
        _.radius -> "radius", // field case class -> field table.
        _.cname  -> "cname"
      )
    }

    val circlesInfo = quote {
      querySchema[CircleInfo](
        "CircleInfo",
        _.id        -> "id",
        _.info1     -> "info1",
        _.info2     -> "info2",
        _.circle_id -> "circle_id"
      )
    }

  }

  before {
    def insertCircleBatch() = quote {
      liftQuery(
        List(
          Circle(id = 1, radius = 1, cname = "cname1"),
          Circle(id = 2, radius = 2, cname = "cname2"),
          Circle(id = 3, radius = 3, cname = "cname3")
        )
      ).foreach(rec => schema.circles.insert(rec))
    }
    ctx.run(insertCircleBatch())

    def insertCircleInfoBatch() = quote {
      liftQuery(
        List(
          CircleInfo(id = 1, info1 = "Info Circle1_1", info2 = "Info Circle2_1", circle_id = 1),
          CircleInfo(id = 2, info1 = "Info Circle1_2", info2 = "Info Circle2_2", circle_id = 2),
          CircleInfo(id = 3, info1 = "Info Circle1_2bis", info2 = "Info Circle2_2bis", circle_id = 2),
          CircleInfo(id = 4, info1 = "Info Circle1_3", info2 = "Info Circle2_3", circle_id = 3)
        )
      ).foreach(cir => schema.circlesInfo.insert(cir))
    }
    ctx.run(insertCircleInfoBatch())

  }

  after {

    def deleteCircleInfo(id: Int) = quote {
      schema.circlesInfo.filter(ci => ci.id == lift(id)).delete
    }

    ctx.run(deleteCircleInfo(1))
    ctx.run(deleteCircleInfo(2))
    ctx.run(deleteCircleInfo(3))
    ctx.run(deleteCircleInfo(4))

    def deleteCircle(id: Int) = quote {
      schema.circles.filter(c => c.id == lift(id)).delete
    }
    ctx.run(deleteCircle(1))
    ctx.run(deleteCircle(2))
    ctx.run(deleteCircle(3))

  }

}
