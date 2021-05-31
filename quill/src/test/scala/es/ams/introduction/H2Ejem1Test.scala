package es.ams.introduction

import io.getquill._

class H2Ejem1Test extends munit.FunSuite {

  val ctx = new H2JdbcContext(SnakeCase, "ctx")
  import ctx._

  case class Product(id: Int, description: String, sku: Long)

  def inserProduct(product: Product) = quote {
    query[Product]
      .insert(
        lift(product)
      )
      .returningGenerated(_.id)
  }

  def selectAllProduct() = quote {
    query[Product]
  }

  test("Basic operations: insert") {
    val returnedIds = ctx.run(inserProduct(Product(id = 0, description = "Description0", sku = 1L)))
    assertEquals(returnedIds, 0)
  }

  test("Basic operations: select all") {
    val returnedIds = ctx.run(inserProduct(Product(id = 0, description = "Description0", sku = 1L)))
    assertEquals(returnedIds, 0)

    val resultSelectAll = ctx.run(selectAllProduct())
    assertEquals(resultSelectAll.size > 0, true)
  }

}
