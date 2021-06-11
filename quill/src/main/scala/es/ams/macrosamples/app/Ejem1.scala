package es.ams.macrosamples.app

import io.getquill.{PostgresJdbcContext, SnakeCase}
import es.ams.macrosamples.Queries
import es.ams.macrosamples.domain.Rectangle

/** Database PostgreSQL
  *
  * DROP TABLE rectangle;
  *
  * CREATE TABLE IF NOT EXISTS Rectangle (
  *    id_rec serial PRIMARY KEY,
  *    length_rec int,
  *    width_rec int
  * );
  *
  * SELECT r.* FROM rectangle r;
  */
object Ejem1 extends App {

  val ctx = new PostgresJdbcContext(SnakeCase, "testPostgresDB") with Queries
  import ctx._

  def example1() = {
    println(s"Inserting...")

    val rectangle1 = Rectangle(id_rec = 11, width_rec = 11, length_rec = 11)
    val insert1    = ctx.run(ctx.insertMacro(rectangle1))
    println(s"Inserted Result=${insert1}")

    val rectangle2 = Rectangle(id_rec = 22, width_rec = 22, length_rec = 22)
    val insert2    = ctx.run(ctx.insertMacro(rectangle2))
    println(s"Inserted Result=${insert2}")

    val rectangle3 = Rectangle(id_rec = 22, width_rec = 222, length_rec = 222)
    println(s"Update=${ctx.run(ctx.update(rectangle3))}")

    val rectangle4 = Rectangle(id_rec = 11, width_rec = 0, length_rec = 0)
    println(s"Delete=${ctx.run(ctx.delete(rectangle4))}")

    val rectangle5 = Rectangle(id_rec = 0, width_rec = 55, length_rec = 55)
    val insert5    = ctx.run(ctx.insertAutoGenerated(rectangle5))
    println(s"Inserted Result=${insert5}")

    val q = quote {
      query[Rectangle]
    }
    val result1 = ctx.run(q)
    // Result: List(Rectangle(22,222,222), Rectangle(1,55,55))
    println(s"Select all=${result1}")
  }

  example1()

}