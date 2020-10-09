package es.doobie.ejemdoc

import java.sql.SQLException

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats.effect._
import cats.implicits._


/**
 * Error Handling
 * --------------
 *
 * https://tpolecat.github.io/doobie/docs/09-Error-Handling.html
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
object Example7ErrorHandling extends App{

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

  def example1(): Unit = {

    List(
      sql"""DROP TABLE IF EXISTS person""",
      sql"""CREATE TABLE person (
          id    SERIAL,
          name  varchar(250) not null unique
        )"""
    ).traverse(_.update.quick).void.unsafeRunSync

    case class Person(id: Long, name: String)


    // Insert OK
    def insert(valueName: String): ConnectionIO[Unit]  = {
      sql"insert into person (name) values ($valueName)".update.withUniqueGeneratedKeys("id", "name")
    }
    insert("bob").quick.unsafeRunSync()


    // Insert KO
    try {
      insert("bob").quick.unsafeRunSync
    } catch {
      case e: java.sql.SQLException =>
        println(s"getMessage=${e.getMessage}")
        println(s"getSQLState=${e.getSQLState}")
    }

//    // ok1
    def safeInsert(valueName: String): ConnectionIO[Either[SQLException, Unit]] =
      insert(valueName).attemptSql

    safeInsert("bob").quick.unsafeRunSync
    //   Left(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry 'bob' for key 'name')
    safeInsert("steve").quick.unsafeRunSync


    // ok2
//    def safeInsert(valueName: String): ConnectionIO[Either[String, String]] =
//      insert(valueName).attemptSql.map {
//        case Right(value) => Right(value.toString)
//        case Left(_) => Left("Error!")
//      }

//    ok3
    def safeInsert3(valueName: String): ConnectionIO[Either[String,Boolean]] =
      insert(valueName).attemptSql.map{
        case Left(ex) => Left(s"Error en inserción: ${ex.getMessage}")
        case Right(_) => Right(true)
      }


    // To ok3
    val insertRows = for {
      aa <- safeInsert3("aa")
      bb <- safeInsert3("bb")
    } yield  { bb  }

    val resultInsertRows: Either[String, Boolean] = insertRows.transact[IO](xa).unsafeRunSync()
    println(s"resultInsertRows=${resultInsertRows}")
    println

  }

  example1()

}
