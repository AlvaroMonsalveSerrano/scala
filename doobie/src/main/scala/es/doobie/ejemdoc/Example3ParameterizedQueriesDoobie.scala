package es.doobie.ejemdoc

//import cats.data.NonEmptyList
//import doobie._
//import doobie.implicits._
import cats.effect.{Blocker, IO}
import doobie.Transactor
import doobie.util.ExecutionContexts

import scala.concurrent.ExecutionContext

/**
 * Parameterized Queries
 * ---------------------
 *
 * https://tpolecat.github.io/doobie/docs/05-Parameterized.html
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
 *
 */

object Example3ParameterizedQueriesDoobie extends App {

  implicit val cs = IO.contextShift(ExecutionContext.global)

  val xa = Transactor.fromDriverManager[IO](
    "com.mysql.jdbc.Driver",
    "jdbc:mysql://localhost:3306/doobie",
    "root",
    "root",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )
//  val y = xa.yolo
//  import y._

  case class Country(code: String, name: String, pop: Int, gnp: Option[Double])

  /**
   * Adding a Parameter
   */
  def example1(): Unit = {

    // Define a query with parameter minPop
    // TODO 2.13
//    def biggerThan(minPop: Int) = sql"""
//        select code, name, population, gnp
//        from country
//        where population > $minPop
//      """.query[Country]
//
//    biggerThan(9).quick.unsafeRunSync()
//    println()

    // TODO 2.13
    // Define a query with multiple parameters
//    def populationIn(range: Range) = sql"""
//          select code, name, population, gnp
//          from country
//          where population > ${range.min}
//          and   population < ${range.max}
//        """.query[Country]
//
//    populationIn(1 to 10).quick.unsafeRunSync()
//    println()

    // TODO 2.13
//    def populationIn2(range: Range, codes: NonEmptyList[String]) = {
//          val q = fr"""
//              select code, name, population, gnp
//              from country
//              where population > ${range.min}
//              and   population < ${range.max}
//              and   """ ++ Fragments.in(fr"code", codes) // code IN (...)
//          q.query[Country]
//    }
//    populationIn2(1 to 10, NonEmptyList.of("POR", "USA", "BRA", "PAK", "GBR")).quick.unsafeRunSync()
//    println()

  }

  def example2(): Unit = {
//    import fs2.Stream

//    val q = """
//      select code, name, population, gnp
//      from country
//      where population > ?
//      and   population < ?
//      """

    println(s"-*- Example2 -*-")
    // Equals to sql"...
    // TODO 2.13
//    def proc(range: Range): Stream[ConnectionIO, Country] =
//      HC.stream[Country](q, HPS.set((range.min, range.max)), 512)
//
//    proc(1 to 10).quick.unsafeRunSync()
  }

  example1()
  example2()
}
