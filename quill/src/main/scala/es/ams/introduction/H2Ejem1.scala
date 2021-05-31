package es.ams.introduction

import io.getquill._

object H2Ejem1 extends App {

  lazy val ctx = new H2JdbcContext(SnakeCase, "ctx")

  import ctx._

  case class Product(id: Int, description: String, sku: Long)

  val q = quote {
    query[Product]
      .insert(
        lift(Product(id = 0, description = "Description0", sku = 1L))
      )
      .returningGenerated(_.id)
  }

  val returnedIds = ctx.run(q)
  println(s"id=${returnedIds}")

  // .filter(p => p.id == 0)
  val myQuery = quote {
    query[Product]
  }

  val resultMyQuery = ctx.run(myQuery)
  println(s"result=${resultMyQuery}")

  val myQueryFilter = quote {
    query[Product].filter(p => p.id == 0)
  }

  val resultmyQueryFilter = ctx.run(myQueryFilter)
  println(s"resultFilter=${resultMyQuery}")

}
