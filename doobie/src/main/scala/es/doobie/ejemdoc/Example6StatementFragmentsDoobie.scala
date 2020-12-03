package es.doobie.ejemdoc

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats.effect._
//import cats.implicits._

//import Fragments.{ in, whereAndOpt }

/** Statement Fragments
  * -------------------
  *
  * https://tpolecat.github.io/doobie/docs/08-Fragments.html
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
  *  Se puede construir un SQL Fragment usando el interpolador fr el cual se comporta como
  *  el interpolador sql.
  *
  *  Se concatenan con ++
  */

object Example6StatementFragmentsDoobie extends App {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "com.mysql.jdbc.Driver",
    "jdbc:mysql://localhost:3306/doobie",
    "root",
    "root",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  val y = xa.yolo
  import y._

  /** SQL literals.
    */
  def example1(): Unit = {
    println(s"-*- Example1 -*-")
    val a = fr"select name from country"
    // a: Fragment = Fragment("select name from country ")

    val b = fr"where code = 'POR'"
    // b: Fragment = Fragment("where code = 'POR' ")

    val c = a ++ b // concatenation by ++
    // c: Fragment = Fragment("select name from country where code = 'POR' ") // concatenation by ++

    c.query[String].unique.quick.unsafeRunSync()

  }

  /** SQL literals.
    */
  def example2(): Unit = {

    println(s"-*- Example2 -*-")
    def whereCode(s: String) = fr"where code = $s"
    val esp                  = whereCode("ESP")
    // esp: Fragment = Fragment("where code = ? ")
    (fr"select name from country" ++ esp).query[String].quick.unsafeRunSync()

    def count(table: String) =
      (fr"select count(*) from" ++ Fragment.const(table)).query[Int].unique
    count("country").quick.unsafeRunSync()

  }

  /** The Fragments Module
    */
  def example3(): Unit = {

    case class Info(name: String, code: String, population: Int)

    // TODO 2.13
//    def select(name: Option[String], pop: Option[Int], codes: List[String], limit: Long) = {
//
//      // Three Option[Fragment] filter conditions.
//      val f1 = name.map(s => fr"name LIKE $s")
//      val f2 = pop.map(n => fr"population > $n")
//      val f3 = codes.toNel.map(cs => in(fr"code", cs))
//
//      // Our final query
//      val q: Fragment =
//        fr"SELECT name, code, population FROM country" ++
//          whereAndOpt(f1, f2, f3)                         ++
//          fr"LIMIT $limit"
//
//
//      // Construct a Query0
//      q.query[Info]
//
//    }

//    select(None, None, Nil, 10).stream.quick.unsafeRunSync()

  }

  example1()
  example2()
  example3()

}
