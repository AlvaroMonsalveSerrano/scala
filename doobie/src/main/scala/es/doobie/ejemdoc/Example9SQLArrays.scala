package es.doobie.ejemdoc

import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats.effect._
import cats.implicits._


/**
 * SQL Arrays
 * ----------
 *
 * https://tpolecat.github.io/doobie/docs/11-Arrays.html
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
 * MySQL does not support Array type!
 */

object Example9SQLArrays extends App {

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

  def example0(): Unit = {

    val drop = sql"DROP TABLE IF EXISTS person".update.quick

    val create =
      sql"""
          CREATE TABLE person (
                id    SERIAL,
                name  varchar(250) not null unique,
                pets varchar(200) not null
          )
      """.update.quick

    (drop *> create).unsafeRunSync

  }

  example0()

}
