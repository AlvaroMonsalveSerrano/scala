package es.doobie.ejemdoc

import cats.effect._
import doobie._
import doobie.implicits._
import doobie.h2._

object Example5DDLInsertingUpdatingH2Doobie extends IOApp {

  val transactor: Resource[IO, H2Transactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      be <- Blocker[IO]    // our blocking EC
      xa <- H2Transactor.newH2Transactor[IO](
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", // connect URL
        "sa",                                   // username
        "",                                     // password
        ce,                                     // await connection here
        be                                      // execute JDBC operations here
      )
    } yield xa


  def printArgument(args: List[String]): Unit = {
    println(s"--")
    args.foreach( println(_) )
    println(s"**")
  }

  def run(args: List[String]): IO[ExitCode] = {
    printArgument(args)
    transactor.use { xa =>

      // Construct and run your server here!
      for {
        n <- sql"select 42".query[Int].unique.transact(xa)
        _ <- IO(println(n))
      } yield ExitCode.Success

    }
  }

}
