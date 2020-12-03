package es.doobie.ejemdoc

//import doobie._
import doobie.implicits._

import cats.effect.{Blocker, IO}
import doobie.Transactor
import doobie.util.ExecutionContexts

import scala.concurrent.ExecutionContext

/** Selecting Data
  * --------------
  *
  * https://tpolecat.github.io/doobie/docs/04-Selecting.html
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
  */
object Example2SelectingDataDoobie extends App {

  implicit val cs = IO.contextShift(ExecutionContext.global)

  val xa = Transactor.fromDriverManager[IO](
    "com.mysql.jdbc.Driver",
    "jdbc:mysql://localhost:3306/doobie",
    "root",
    "root",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  /** Reading Rows into Collections
    */
  def example1(): Unit = {
    println(s"-*- Example1 -*-")
    sql"select name from country"
      .query[String]    // Query0[String]
      .to[List]         // ConnectionIO[List[String]]
      .transact(xa)     // IO[List[String]]
      .unsafeRunSync()  // List[String]
      .take(5)          // List[String]
      .foreach(println) // Unit

  }

  /** Internal Streaming
    *
    * El Stream utiliza un  fs2(https://github.com/typelevel/fs2) Stream[ConnectionIO, String] el cual procesa filas
    * conforme las va leyendo; una vez leídas 5 (take(5)), limpia el stream y vuelve a leer otras cinco. Esta operación
    * es más eficiente que la anterior porque no lee todas las filas existentes.
    */
  def example2(): Unit = {
    println(s"-*- Example2 -*-")
    sql"select name from country"
      .query[String] // Query0[String]
      .stream        // Stream[ConnectionIO, String]
      .take(5)       // Stream[ConnectionIO, String]
      .compile
      .toList           // ConnectionIO[List[String]]
      .transact(xa)     // IO[List[String]]
      .unsafeRunSync()  // List[String]
      .foreach(println) // Unit

  }

  def example3(): Unit = {
    println(s"-*- Example3 -*-")

    val y = xa.yolo // a stable reference is required. OJO: xa es un Transactor
    import y._ // Permite utilizar quick

    sql"select name from country"
      .query[String] // Query0[String]
      .stream        // Stream[ConnectionIO, String]
      .take(5)       // Stream[ConnectionIO, String]
      .quick         // IO[Unit]
      .unsafeRunSync()

  }

  /** Multi-Column Queries
    */
  def example4(): Unit = {

    // TODO 2.13
//    val y = xa.yolo // a stable reference is required. OJO: xa es un Transactor
//    import y._ // Permite utilizar quick

    println(s"-*- Example4 -*-")
// TODO 2.13
//    sql"select code, name, population, gnp from country"
//      .query[(String, String, Int, Option[Double])]
//      .stream
//      .take(5)
//      .quick
//      .unsafeRunSync()

    // Doobie soporta el mapeo de filas por tipos de columnas.
// TODO 2.13
//    import shapeless._
//
//    sql"select code, name, population, gnp from country"
//      .query[String :: String :: Int :: Option[Double] :: HNil]
//      .stream
//      .take(5)
//      .quick
//      .unsafeRunSync()

// TODO 2.13
//    // Con shapeless record.
//    import shapeless.record.Record
//
//    type Rec = Record.`"code" -> String, "name" -> String, "pop" -> Int, "gnp" -> Option[Double]`.T
//
//    sql"select code, name, population, gnp from country"
//      .query[Rec]
//      .stream
//      .take(5)
//      .quick
//      .unsafeRunSync()

//    import scala.language.implicitConversions

    // Mapeo a case class.
    case class Country(code: String, name: String, pop: Int, gnp: Option[Double])

    // TODO 2.13
//    sql"select code, name, population, gnp from country"
//      .query[Country]
//      .stream
//      .take(5)
//      .quick
//      .unsafeRunSync()

    // Se puede mapear anidar casee class.
    case class Code(code: String)
    case class Country2(name: String, pop: Int, gnp: Option[Double])
    // TODO 2.13
//    sql"select code, name, population, gnp from country"
//      .query[(Code, Country2)]
//      .stream
//      .take(5)
//      .quick
//      .unsafeRunSync()

    // TODO 2.13
    // Mapear el resultado a un Map. Code es PK.
//    sql"select code, name, population, gnp from country"
//      .query[(Code, Country2)]
//      .stream.take(5)
//      .compile.toList
//      .map(_.toMap)
//      .quick
//      .unsafeRunSync()
  }

  /** Final Streaming
    *
    * @return
    */
  def example5(): Unit = {
    println(s"-*- Example5 -*-")

    case class Code(code: String)
    case class Country2(name: String, pop: Int, gnp: Option[Double])

//    val p: Stream[IO, Country2] = {
//      sql"select name, population, gnp from country"
//        .query[Country2] // Query0[Country2]
//        .stream          // Stream[ConnectionIO, Country2]
//        .transact(xa)    // Stream[IO, Country2]
//    }
//    // p: Stream[IO, Country2] = Stream(..)
//
//    p.take(5).compile.toVector.unsafeRunSync.foreach(println)

  }

  /** Diving Deeper
    */
  def example6(): Unit = {

//    import cats.implicits._
//    val y = xa.yolo // a stable reference is required. OJO: xa es un Transactor
//    import y._ // Permite utilizar quick

    println(s"-*- Example6 -*-")
    case class Code(code: String)
    case class Country2(name: String, pop: Int, gnp: Option[Double])

    // TODO 2.13
//    val proc = HC.stream[(Code, Country2)](
//      "select code, name, population, gnp from country", // statement
//      ().pure[PreparedStatementIO],                      // prep (none)
//      512                                                // chunk size
//    )
//
//    proc.take(5)        // Stream[ConnectionIO, (Code, Country2)]
//      .compile.toList // ConnectionIO[List[(Code, Country2)]]
//      .map(_.toMap)   // ConnectionIO[Map[Code, Country2]]
//      .quick
//      .unsafeRunSync()

  }

  example1()
  example2()
  example3()
  example4()
  example6()

}
