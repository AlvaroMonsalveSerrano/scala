package es.ams.modulelayer

//import zio._
import zio.test._
//import zio.random.Random
import Assertion._

/** https://timpigden.github.io/_pages/zlayer/Examples.html
  */
object ModuleLayerTest extends DefaultRunnableSpec {

  import es.ams.modulelayer.ModuleLayerExample2._ //{firstNames, Names} // , Teams

  def nameTest = testM("name test") {
    for {
      // names == package names
      name <- names.randomName
    } yield {
      assert(firstNames.contains(name))(equalTo(true))
    }
  }

  def justTeamsTest = testM("small teams test") {
    for {
      team <- teams.pickTeam(1)
    } yield {
      assert(team.size)(equalTo(1))
    }
  }

  def inMyTeam = testM("combines names ans teams") {
    for {
      name <- names.randomName
      team <- teams.pickTeam(5)
      _ =
        if (team.contains(name))
          println("one of mine")
        else
          println("not mine")
    } yield { assertCompletes }
  }

  def wonLastYear = testM("won last year") {
    for {
      team <- teams.pickTeam(5)
      ly   <- history.wonLastYear(team)
    } yield { assertCompletes }
  }

  val individuall = suite("individually")(
    suite("Needs Names")(
      nameTest
    ).provideCustomLayerShared(Names.live),
    suite("needs just Team")(
      justTeamsTest
    ).provideCustomLayer(Names.live >>> Teams.live),
    suite("needs Names and Teams")(
      inMyTeam
    ) provideCustomLayer (Names.live ++ (Names.live >>> Teams.live)),
    suite("needs History and Teams") {
      wonLastYear
    }.provideCustomLayerShared(
      (Names.live >>> Teams.live) ++
        (Names.live >>> Teams.live >>> History.live)
    )
  )

  // Don´t compile in spec function.
  val allTogether = suite("all together")(
    suite("Needs names")(
      nameTest
    ),
    suite("needs just Team")(
      justTeamsTest
    ),
    suite("needs Names and Teams")(
      inMyTeam
    ),
    suite("needs History and Teams")(
      wonLastYear
    )
  ).provideCustomLayerShared(
    Names.live ++
      (Names.live >>> Teams.live) ++
      (Names.live >>> Teams.live >>> History.live)
  )

  //
  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = {
    individuall
//    allTogether
  }

}
