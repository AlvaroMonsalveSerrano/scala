package es.doobie.ejemdoc

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.log.LogEvent
import cats.effect._


/**
 * Logging
 * -------
 *
 * https://tpolecat.github.io/doobie/docs/10-Logging.html
 *
 * + Docker MySQL:
 *
 * # run mysql
 * docker run --name mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 -d mysql:8.0.0
 *
 * # connect to mysql container
 * docker exec -it mysql bash
 *
 * # connect to mysql
 * mysql -uroot -p
 *
 * # create database
 * CREATE DATABASE IF NOT EXISTS doobie;
 *
 *
 * + SQL:
 *
 * CREATE TABLE doobie.country (
 * code       character(3)  NOT NULL,
 * name       text          NOT NULL,
 * population integer       NOT NULL,
 * gnp        numeric(10,2)
 * -- more columns, but we won't use them here
 * );
 *
 * INSERT INTO country (code, name, population, gnp) VALUES ( 'ESP', 'España', 10, 10.10);
 * INSERT INTO country (code, name, population, gnp) VALUES ( 'POR', 'Portugal', 9, 9.9);
 * INSERT INTO country (code, name, population, gnp) VALUES ( 'FRA', 'Francia', 8, 8.8);
 *
 * SELECT * FROM country;
 */

object Example8Logging extends App {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "com.mysql.jdbc.Driver",
    "jdbc:mysql://localhost:3306/doobie",
    "root",
    "root",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )


  /**
   * Basic Statement Logging
   *
   */
  def example1(): Unit = {

    /**
     * Console output:
     *
     * INFORMACIÓN: Successful Statement Execution:
     *
     * select name, code from country where name like ?
     *
     * arguments = [E%]
     * elapsed = 8 ms exec + 19 ms processing (28 ms total)
     *
     * @param pat
     * @return
     */
    def byName(pat: String): IO[List[(String, String)]] = {
      sql"select name, code from country where name like $pat"
        .queryWithLogHandler[(String, String)](LogHandler.jdkLogHandler)
        .to[List]
        .transact(xa)
    }

    println(s"-*- Example1 -*-")
    byName("E%").unsafeRunSync()
    println

  }

  /**
   * Implicit Logging
   *
   */
  def example2(): Unit = {

    implicit val han = LogHandler.jdkLogHandler

    def byName2(pat: String): IO[List[(String, String)]] = {
      sql"select name, code from country where name like $pat"
        .query[(String, String)] // handler will be picked up here
        .to[List]
        .transact(xa)
    }

    println(s"-*- Example2 -*-")
    byName2("E%").unsafeRunSync()
    println

  }

  def example3(): Unit = {

    case class LogHandler(unsafeRun: LogEvent => Unit)

    val nop = doobie.LogHandler(_ => ())
    val trivial = doobie.LogHandler(e => Console.println("*** " + e))

    println(s"-*- Example3 -*-")
    sql"select 42".queryWithLogHandler[Int](trivial).unique.transact(xa).unsafeRunSync
    sql"select 52".queryWithLogHandler[Int](nop).unique.transact(xa).unsafeRunSync
    sql"select 42".queryWithLogHandler[Int](trivial).unique.transact(xa).unsafeRunSync
    println

  }


  /**
   * Writing Your Own LogHandler
   *
   */
  def example4(): Unit = {

    import java.util.logging.Logger
    import doobie.util.log.{ Success, ProcessingFailure, ExecFailure }

    val jdkLogHandler: LogHandler = {
      val jdkLogger = Logger.getLogger(getClass.getName)
      LogHandler {

        case Success(s, a, e1, e2) =>
          jdkLogger.info(s"""Successful Statement Execution:
                            |
                            |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                            |
                            | arguments = [${a.mkString(", ")}]
                            |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (${(e1 + e2).toMillis} ms total)
      """.stripMargin)

        case ProcessingFailure(s, a, e1, e2, t) =>
          jdkLogger.severe(s"""Failed Resultset Processing:
                              |
                              |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                              |
                              | arguments = [${a.mkString(", ")}]
                              |   elapsed = ${e1.toMillis} ms exec + ${e2.toMillis} ms processing (failed) (${(e1 + e2).toMillis} ms total)
                              |   failure = ${t.getMessage}
      """.stripMargin)

        case ExecFailure(s, a, e1, t) =>
          jdkLogger.severe(s"""Failed Statement Execution:
                              |
                              |  ${s.linesIterator.dropWhile(_.trim.isEmpty).mkString("\n  ")}
                              |
                              | arguments = [${a.mkString(", ")}]
                              |   elapsed = ${e1.toMillis} ms exec (failed)
                              |   failure = ${t.getMessage}
      """.stripMargin)

      }

    }

    def byName4(pat: String): IO[List[(String, String)]] = {
      sql"select name, code from country where name like $pat"
        .queryWithLogHandler[(String, String)](jdkLogHandler)
        .to[List]
        .transact(xa)
    }

    println(s"-*- Example4 -*-")
    byName4("E%").unsafeRunSync()
    byName4("X%").unsafeRunSync()
    println

  }

  example1()
  example2()
  example3()
  example4()

}
