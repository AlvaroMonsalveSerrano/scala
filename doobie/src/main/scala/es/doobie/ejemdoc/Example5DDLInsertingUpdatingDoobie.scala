package es.doobie.ejemdoc

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats.effect._
import cats.implicits._

/** DDL, Inserting, and Updating
  * ----------------------------
  *
  * https://tpolecat.github.io/doobie/docs/07-Updating.html
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
object Example5DDLInsertingUpdatingDoobie extends App {

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

  /** Data definition.
    */
  def example1(): Unit = {

    println(s"-*- Example1 -*-")
    val drop =
      sql"""
          DROP TABLE IF EXISTS person
        """.update.run

    val create =
      sql"""
        CREATE TABLE person (
          id   SERIAL,
          name text,
          age  SMALLINT
        )
      """.update.run

    // mapN. Semigroupal de Applicative.
    (drop, create).mapN(_ + _).transact(xa).unsafeRunSync()

  }

  /** Inserting
    */
  def example2(): Unit = {

    println(s"-*- Example2 -*-")
    def insert1(name: String, age: Option[Short]): Update0 =
      sql"insert into person (name, age) values ($name, $age)".update

    insert1("Alice", Some(12)).run.transact(xa).unsafeRunSync()
    insert1("Bob", None).quick.unsafeRunSync()

    case class Person(id: Long, name: String, age: Option[Short])

    // TODO 2.13
//    sql"""
//          select
//            id, name, age
//          from person
//    """.query[Person].stream.quick.unsafeRunSync()

  }

  /** Updating
    */
  def example3(): Unit = {

    println(s"-*- Example3 -*-")
    case class Person(id: Long, name: String, age: Option[Short])
    sql"update person set age = 15 where name = 'Alice'".update.quick.unsafeRunSync()

    // TODO 2.13
//    sql"select id, name, age from person".query[Person].stream.quick.unsafeRunSync()

  }

  /** Retrieving Results
    */
  def example4(): Unit = {

    println(s"-*- Example4 -*-")
    case class Person(id: Long, name: String, age: Option[Short])

    // TODO 2.13
//    def insert2(name: String, age: Option[Short]): ConnectionIO[Person] =
//      for {
//        _  <- sql"insert into person (name, age) values ($name, $age)".update.run
//        id <- sql"select LAST_INSERT_ID()".query[Long].unique
//        p  <- sql"select id, name, age from person where id = $id".query[Person].unique
//      } yield p
//
//
//    insert2("Jimmy", Some(42)).quick.unsafeRunSync()

  }

  /** Batch Updates
    */
  def example5(): Unit = {

    println(s"-*- Example5 -*-")
    case class Person(id: Long, name: String, age: Option[Short])

    // TODO 2.13
//    type PersonInfo = (String, Option[Short])

//    def insertMany(ps: List[PersonInfo]): ConnectionIO[Int] = {
//      val sql = "insert into person (name, age) values (?, ?)"
//      Update[PersonInfo](sql).updateMany(ps)
//    }

//    val data = List[PersonInfo](
//      ("Frank", Some(12)),
//      ("Daddy", None))

//    insertMany(data).quick.unsafeRunSync()

//    sql"select id, name, age from person".query[Person].stream.quick.unsafeRunSync()
  }

  example1()
  example2()
  example3()
  example4()
  example5()

}
