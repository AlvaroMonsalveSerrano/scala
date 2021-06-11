package es.ams.iomonad.app

import es.ams.macrosamples.domain.Rectangle
import es.ams.iomonad.QueriesIOMonad
import io.getquill.{PostgresJdbcContext, SnakeCase}

/**  PostgreSQL database:
  *
  * DROP TABLE rectangle;
  *
  * CREATE TABLE IF NOT EXISTS Rectangle (
  *     id_rec serial PRIMARY KEY,
  *     length_rec int,
  *     width_rec int
  * );
  *
  * SELECT r.* FROM rectangle r;
  */
object Ejem1IO extends App {
  val ctx = new PostgresJdbcContext(SnakeCase, "testPostgresDB") with QueriesIOMonad
  import ctx._

  /** IOMonad
    */
  def example1(): Unit = {
    val rectangle1 = Rectangle(id_rec = 11, length_rec = 11, width_rec = 11)
    val rectangle2 = Rectangle(id_rec = 22, length_rec = 22, width_rec = 22)
    val program1 = ctx
      .runIO(ctx.insertAutoGenerated(rectangle1))
      .flatMap(_ => ctx.runIO(ctx.insertAutoGenerated(rectangle2)))
      .flatMap { _ =>
        ctx.runIO(ctx.selectAllRectangle())
      }

    println(s"Example1 program1=${program1}")
    ctx.performIO(program1)
  }

  def example2(): Result[RunQueryResult[Rectangle]] = {
    val rectangle1 = Rectangle(id_rec = 11, length_rec = 11, width_rec = 11)
    val rectangle2 = Rectangle(id_rec = 22, length_rec = 22, width_rec = 22)

    def program() = {
      val program1 = for {
        rec1 <- ctx.runIO(ctx.insertAutoGenerated(rectangle1))
        rec2 <- ctx.runIO(ctx.insertAutoGenerated(rectangle2))
        lst  <- ctx.runIO(ctx.selectAllRectangle())
      } yield (lst)

      println(s"Example2 program1=${program1}")
      ctx.performIO(program1.transactional)

    }

    program()
  }

  example1()
//  example2()
}