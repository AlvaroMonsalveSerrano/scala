package es.ams.introduction

object IntroductionEjem1 extends App {

  import io.getquill._

  val ctx = new SqlMirrorContext(MirrorSqlDialect, Literal)

  // El contexto provee de todos los tipos, métodos y Encoder/Decoder necesarios.
  import ctx._

  // quote. PArte de código que puede
  val pi      = quote(3.14159)
  val result1 = ctx.run(pi)
  println(s"result1=${result1}") // SELECT 3.14159,Row(List())

  case class Circle(radius: Float)

  val q: Quoted[Query[Circle]] = quote {
    query[Circle].filter(c => c.radius > 10)
  }

  val result2 = ctx.run(q)
  println(s"result2=${result2}") // SELECT c.radius FROM Circle c WHERE c.radius > 10,Row(List())

}
