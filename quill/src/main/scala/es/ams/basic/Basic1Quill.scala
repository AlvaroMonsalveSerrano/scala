package es.ams.basic

object Basic1Quill extends App {

  import io.getquill._

  val ctx = new SqlMirrorContext(PostgresDialect, SnakeCase)

  import ctx._

  case class Person(name: String, age: Int)

  val m = ctx.run(query[Person].filter(p => p.name == "John"))

  println(m.string)

}
