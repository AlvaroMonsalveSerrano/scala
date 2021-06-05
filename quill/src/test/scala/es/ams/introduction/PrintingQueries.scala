package es.ams.introduction

class PrintingQueries extends BaseTest {
  import ctx._

  "Printing queries" should "print select" in {

    def selectCircle() = quote {
      schema.circles
    }
    val result = ctx.translate(selectCircle())
    assert(result.size > 0)
    assert(result.equals("SELECT x.id, x.radius, x.cname FROM Circle x"))

  }

  it should "print select 2" in {

    def selectCnameCircle() = quote {
      schema.circles.map(circle => circle.cname)
    }
    val result = ctx.translate(selectCnameCircle())
    assert(result.size > 0)
    assert(result.equals("SELECT circle.cname FROM Circle circle"))

  }

}
