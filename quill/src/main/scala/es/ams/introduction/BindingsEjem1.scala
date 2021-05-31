package es.ams.introduction

object BindingsEjem1 extends App {

  import io.getquill._

  val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)

  import ctx._

  case class Circle(radius: Float)

  // 1.- Enlace de un campo por valor.
  def biggerThan(i: Float) = quote {
    query[Circle].filter(r => r.radius > lift(i))
  }
  ctx.run(biggerThan(10)) // SELECT r.radius FROM Circle r WHERE r.radius > ?

  // 2.- Enlace d un conjunto
  def find(radiusList: List[Float]) = quote {
    query[Circle].filter(r => liftQuery(radiusList).contains(r.radius))
  }
  ctx.run(find(List(1.1f, 1.2f)))
  // SELECT r.radius FROM Circle r WHERE r.radius IN (?)

  // 3.- Enlace en batch.
  def insert(circles: List[Circle]) = quote {
    liftQuery(circles).foreach(c => query[Circle].insert(c))
  }
  ctx.run(insert(List(Circle(1.1f), Circle(1.2f))))
  // INSERT INTO Circle (radius) VALUES (?)

}
