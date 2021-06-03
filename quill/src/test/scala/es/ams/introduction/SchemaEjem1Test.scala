package es.ams.introduction

import io.getquill.{H2JdbcContext, SnakeCase}

class SchemaEjem1Test extends munit.FunSuite {

  val ctx = new H2JdbcContext(SnakeCase, "ctx")
  import ctx._

  case class Rectangle(id: Int, length: Int, width: Int)

//  Schema customization
  val rectangles = quote {
    querySchema[Rectangle](
      "Rectangle",
      _.id     -> "id_rec", // field case class -> field table.
      _.length -> "length_rec",
      _.width  -> "width_rec"
    )
  }

  def insertRectangle(rectangle: Rectangle) = quote {
    rectangles
      .insert(
        lift(rectangle)
      )
      .returningGenerated(_.id)
  }

//  returning function not support in H2
//  def insertRectangleWithEntityResult(rectangle: Rectangle) = quote {
//    rectangles
//      .insert(
//        lift(rectangle)
//      )
//      .returning(rec => (rec.id, rec.width, rec.length))
//  }

  def insertDataTest() = {
    ctx.run(rectangles)
    ctx.run(insertRectangle(new Rectangle(id = 1, length = 2, width = 3)))
    ctx.run(insertRectangle(new Rectangle(id = 2, length = 4, width = 5)))
    ctx.run(insertRectangle(new Rectangle(id = 3, length = 6, width = 7)))
  }

  def selectAllRectangle() = quote {
    rectangles
  }

  insertDataTest()

//  returning function not support in H2
//  def updateRectangle(rectangle: Rectangle) = quote {
//    query[Rectangle].update(lift(rectangle)).returning(rec => (rec.id, rec.length, rec.width))
//  }

  test("Rectangle. Create schema, select all rectangle with the schema created in code") {
    val result = ctx.run(selectAllRectangle())
    assertEquals(result.size, 3)
  }

//  returning function not support in H2
  test("Rectangle. Insert with tuple result".flaky) {
//    val result = ctx.run(insertRectangleWithEntityResult(Rectangle(4, 7, 8)))
//    assertEquals(result._1, 4)
//    assertEquals(result._2, 7)
//    assertEquals(result._3, 8)
  }

  //  returning function not support in H2
  test("Rectangle. Update Rectangle with id = 2 ".flaky) {
//    val result = ctx.run(updateRectangle(Rectangle(2, 9, 9)))
//    assertEquals(result._1, 2)
//    assertEquals(result._2, 9)
//    assertEquals(result._2, 9)
  }

}
