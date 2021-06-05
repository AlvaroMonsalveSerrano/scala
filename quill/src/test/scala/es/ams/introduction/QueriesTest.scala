package es.ams.introduction

import io.getquill.{Ord, Query}

class QueriesTest extends BaseTest {

  import ctx._

  "Queries" should "Map Operation" in {

    def mapExample() = quote {
      schema.circles.map(c => c.cname)
    }

    val result = ctx.run(mapExample())
    assert(result.size == 3)

  }

  it should "flatMap operation" in {
    def myFlatMap(name: String) = quote {
      schema.circles
        .filter(circle => circle.cname == lift(name))
//        .flatMap(circle => schema.circlesInfo.filter(cInfo => circle.id == cInfo.circle_id)) // join
        .flatMap(circle => schema.circlesInfo.filter(cInfo => cInfo.info1 == "Info Circle1_2bis"))

    }
    val result = ctx.run(myFlatMap("cname2"))
    assert(result.size == 1)
  }

  it should "sortBy" in {
    // SELECT p.id, p.radius, p.cname FROM Circle p ORDER BY p.cname DESC NULLS LAST
    def sortCircle() = quote {
      schema.circles.sortBy(circle => circle.cname)(Ord.descNullsLast)
    }

    val result = ctx.run(sortCircle())
    assert(result.size == 3)
    assert(result.head.cname == "cname3")
  }

  it should "groupBy" in {
    // SELECT p.circle_id, COUNT(*) FROM CircleInfo p GROUP BY p.circle_id
    def groupByCircleInfoCircleId() = quote {
      schema.circlesInfo.groupBy(circleInfo => circleInfo.circle_id).map { case (circleInfo, circleInfoGroup) =>
        (circleInfo, circleInfoGroup.size)
      }
    }

    val result = ctx.run(groupByCircleInfoCircleId())
    assert(result.size == 3)
  }

  it should "contains" in {
    // 1
    // SELECT p.id, p.info1, p.info2, p.circle_id FROM CircleInfo p WHERE p.id IN (?, ?)
    def containsCircle(setCircles: Set[Int]) = quote {
      schema.circlesInfo.filter(circle => liftQuery(setCircles).contains(circle.id))
    }
    val result = ctx.run(containsCircle(List(1, 2).toSet))
    assert(result.size == 2)

    // 2
    val queryContains = quote { (setId: Query[Int]) =>
      schema.circlesInfo.filter(circles => setId.contains(circles.id))
    }
    val resultQueryContains = ctx.run(queryContains(liftQuery(List(2).toSet)))
    assert(resultQueryContains.size == 1)
  }

  it should "select distinct" in {
    val querySelectDistinct = quote {
      schema.circles.map(circle => circle.cname).distinct
    }
    val result = ctx.run(querySelectDistinct)
    assert(result.size == 3)
  }

  it should "nested" in {
    val queryNested = quote {
      schema.circlesInfo.filter(circle => circle.circle_id == 2).nested.map(circle => circle.info1)
    }
    val result = ctx.run(queryNested)
    assert(result.size == 2)
  }

  it should "join applicative" in {
    // join or joinLeft or joinRight
    def joinCircles() = quote {
      schema.circles.join(schema.circlesInfo).on(_.id == _.circle_id)
    }

    val result = ctx.run(joinCircles())
    assert(result.size == 4)
  }

  it should "join implicit" in {
    val join = quote {
      for {
        circle     <- schema.circles
        circleInfo <- schema.circlesInfo if (circle.id == circleInfo.circle_id)
      } yield (circle.cname, circleInfo.info1)
    }

    val result = ctx.run(join)
    assert(result.size == 4)
  }

  it should "Flat joins" in {
    val join = quote {
      for {
        circle     <- schema.circles
        circleInfo <- schema.circlesInfo.join(_.circle_id == circle.id)
      } yield (circle.cname, circleInfo.info1)
    }

    val result = ctx.run(join)
    println(s"result=${result}")
    assert(result.size == 4)
  }

}
