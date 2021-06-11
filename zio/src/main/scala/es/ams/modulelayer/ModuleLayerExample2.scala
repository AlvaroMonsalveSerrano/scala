package es.ams.modulelayer

import zio._
import zio.random.Random

object ModuleLayerExample2 {

  val firstNames = Vector("Ed", "Jane", "Joe", "Linda", "Sue", "Tim", "Tom")

  type Names = Has[Names.Service]

  object Names {
    trait Service {
      def randomName: UIO[String]
    }

    case class NamesImpl(random: Random.Service) extends Names.Service {
      println(s"created namesImpl")
      override def randomName: UIO[String] = random.nextIntBounded(firstNames.size).map(firstNames(_))
    }

    val live: ZLayer[Random, Nothing, Names] = ZLayer.fromService(NamesImpl)
  }

  type Teams = Has[Teams.Service]

  object Teams {
    trait Service {
      def pickTeam(size: Int): UIO[Set[String]]
    }

    case class TeamsImpl(names: Names.Service) extends Service {
      override def pickTeam(size: Int): UIO[Set[String]] =
        ZIO.collectAll(0.until(size).map { _ => names.randomName }).map(_.toSet)
    }

    val live: ZLayer[Names, Nothing, Teams] = ZLayer.fromService(TeamsImpl)
  }

  type History = Has[History.Service]

  object History {
    trait Service {
      def wonLastYear(team: Set[String]): Boolean
    }

    case class HistoryImpl(lastTearsWinners: Set[String]) extends Service {
      override def wonLastYear(team: Set[String]): Boolean = lastTearsWinners == team
    }

    val live: ZLayer[Teams, Nothing, History] = ZLayer.fromServiceM { teams =>
      teams.pickTeam(5).map(nt => HistoryImpl(nt))
    }
  }

}

import ModuleLayerExample2.Names
package object names {
  def randomName = ZIO.accessM[Names](_.get.randomName)
}

import ModuleLayerExample2.Teams
package object teams {
  def pickTeam(nPicks: Int) = ZIO.accessM[Teams](_.get.pickTeam(nPicks))
}

import ModuleLayerExample2.History
package object history {
  def wonLastYear(team: Set[String]) = ZIO.access[History](_.get.wonLastYear(team))
}
