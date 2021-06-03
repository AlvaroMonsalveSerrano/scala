package es.ams.introduction

import io.getquill._

/** docker run  --name postgres -e POSTGRES_DB=prueba  -e POSTGRES_PASSWORD=password -i -t -p 5436:5432  postgres:latest
  *
  * CREATE SCHEMA IF NOT EXISTS quill;
  * DROP TABLE quill.rectangle;
  * CREATE TABLE IF NOT EXISTS quill.Rectangle (
  *   id_rec serial PRIMARY KEY,
  *   length_rec int,
  *   width_rec int
  * );
  */
object PostgresEjem1 extends App {

  val ctx = new PostgresJdbcContext(SnakeCase, "testPostgresDB")
  import ctx._

  case class Rectangle(id: Int, length: Int, width: Int)

  // -- Quill functions

  //  Schema customization
  val rectangles = quote {
    querySchema[Rectangle](
      "quill.Rectangle", // schema.table_name
      _.id     -> "id_rec", // field case class -> field table rectangle.
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
  def insertRectangleWithEntityResult(rectangle: Rectangle) = quote {
    rectangles
      .insert(
        lift(rectangle)
      )
      .returning(rec => (rec.id, rec.width, rec.length))
  }

  def insertBatch() = quote {
    liftQuery(
      List(
        Rectangle(id = 44, width = 44, length = 44),
        Rectangle(id = 55, width = 55, length = 55),
        Rectangle(id = 66, width = 66, length = 66),
        Rectangle(id = 77, width = 77, length = 77)
      )
    ).foreach(rec => rectangles.insert(rec).returningGenerated(_.id))
  }

  def selectAllRectangle() = quote {
    rectangles
  }

  def updateRectangle(rectangle: Rectangle) = quote {
    rectangles
      .filter(_.id == lift(rectangle.id))
      .update(lift(rectangle))
      .returning(rec => (rec.id, rec.length, rec.width))
  }

  def deleteRectangle(idRectangle: Int) = quote {
    rectangles.filter(rec => rec.id == lift(idRectangle)).delete
  }

  // --

  /** Insert Rectangle
    * @return
    */
  def example1() = {
    println("-*-*- INSERT RECTANGLE -*-*-")
    ctx.run(rectangles)
    ctx.run(insertRectangle(new Rectangle(id = 1, length = 2, width = 3)))
    ctx.run(insertRectangle(new Rectangle(id = 2, length = 4, width = 5)))
    ctx.run(insertRectangle(new Rectangle(id = 3, length = 6, width = 7)))

  }

  def example1_1(): Unit = {
    println("-*-*- BATCH INSERT RECTANGLE -*-*-")
    ctx.run(insertBatch())
    val result = ctx.run(selectAllRectangle())
    println(s"result=${result}")
    println()
  }

  /** Select * from rectangule
    */
  def example2() = {
    println("-*-*- SELECT ALL -*-*-")
    val result = ctx.run(selectAllRectangle())
    println(s"result=${result}")
    println()
  }

  def example3(): Unit = {
    println(s"-*-*- Insert rectangule -*-*-")
    val resultInsert = ctx.run(insertRectangle(Rectangle(id = 0, width = 8, length = 8)))
    println(s"resultInsert=${resultInsert}")
    println()

    val result = ctx.run(selectAllRectangle())
    println(s"result=${result}")
    println()
  }

  def example4(): Unit = {
    println(s"-*-*- Filter rectangle -*-*-")
    val result = ctx.run(rectangles.filter(_.id == lift(2)))
    println(s"result=${result}")
    println()
  }

  def example5(): Unit = {
    println(s"-*-*- Update rectangule -*-*-")
    val resultUpdate = ctx.run(updateRectangle(Rectangle(id = 2, width = 666, length = 666)))
    println(s"resultUpdate=${resultUpdate}")
    println()

    val result = ctx.run(selectAllRectangle())
    println(s"result=${result}")
    println()

  }

  def example6(): Unit = {
    println(s"-*-*- Delete rectangule -*-*-")
    val result = ctx.run(deleteRectangle(1))
    println(s"resultDelete=${result}")

    val resultSelectAll = ctx.run(selectAllRectangle())
    println(s"result=${resultSelectAll}")
    println()

  }

  example1()
  example1_1()
  example2()
  example3()
  example4()
  example5()
  example6()

}
