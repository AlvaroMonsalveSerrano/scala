package es.ams.iomonad.app

import es.ams.iomonad.QueriesIOMonad
import es.ams.macrosamples.domain.Rectangle
import io.getquill.{PostgresAsyncContext, SnakeCase}

import scala.concurrent.{Future}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/** DROP TABLE rectangle;
  *
  * CREATE TABLE IF NOT EXISTS Rectangle (
  *     id_rec serial PRIMARY KEY,
  *     length_rec int,
  *     width_rec int
  * );
  *
  * SELECT r.* FROM rectangle r;
  */
object Ejem2IO extends App {

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  // It is different to PostgresContext configuration.
  val ctx = new PostgresAsyncContext(SnakeCase, "asynpostgres") with QueriesIOMonad
  import ctx._

  val rectangle1 = Rectangle(id_rec = 11, length_rec = 11, width_rec = 11)
  val rectangle2 = Rectangle(id_rec = 22, length_rec = 22, width_rec = 22)

  def example1() = {
    def insert(rec: Rectangle): Future[Long] =
      ctx.run(query[Rectangle].insert(lift(rec)))

    val result = Await.result(insert(rectangle1), Duration.Inf)
    println(s"result=${result}")

  }

  def example2() = {
    val program1 = ctx.runIO(ctx.insertAutoGenerated(rectangle1))

    val result = Await.result(ctx.performIO(program1), Duration.Inf)
    println(s"result=${result}")

  }

  def example3() = {
    val program1 = for {
      rec1 <- ctx.runIO(ctx.insertAutoGenerated(rectangle1))
      rec2 <- ctx.runIO(ctx.insertAutoGenerated(rectangle2))
      lst  <- ctx.runIO(ctx.selectAllRectangle())
    } yield { lst }

    val result = Await.result(ctx.performIO(program1), Duration.Inf)
    println(s"result=${result}")

  }

//  example1()
//  example2()
  example3()
}
