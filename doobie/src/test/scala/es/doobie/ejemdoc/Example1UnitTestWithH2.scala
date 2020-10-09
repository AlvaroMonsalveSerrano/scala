package es.doobie.ejemdoc

import doobie._
import doobie.implicits._
import cats.effect._

import scala.concurrent.ExecutionContext
import cats.implicits._
import doobie.Transactor
import doobie.util.ExecutionContexts
import org.scalatest.{BeforeAndAfterAll, funsuite, matchers}


/**
  * Test with H2
  */
class Example1UnitTestWithH2 extends funsuite.AnyFunSuite
  with matchers.must.Matchers
  with doobie.scalatest.IOChecker
  with BeforeAndAfterAll{

  implicit private val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val transactor: Transactor[IO]  = Transactor.fromDriverManager[IO](
    "com.mysql.jdbc.Driver",
    "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", // connect URL
    "sa",                                   // username
    "",                                     // password
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  val y = transactor.yolo
  import y._

  case class Person(id: Int, name: Option[String], age: Option[Short])

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    val drop =
      sql"""
          DROP TABLE IF EXISTS person
        """.update.run

    val create =
      sql"""
        CREATE TABLE person (
          id   SERIAL,
          name varchar(100),
          age  SMALLINT
        )
      """.update.run

    // mapN. Semigroupal de Applicative.
    (drop, create).mapN(_ + _).transact(transactor).unsafeRunSync
  }

  override protected def afterAll(): Unit = {
    super.afterAll()

    val drop =
      sql"""
          DROP TABLE IF EXISTS person
        """.update.run

    // mapN. Semigroupal de Applicative.
    (drop).transact(transactor).unsafeRunSync
  }

  test("Test trivial1 operation"){
    check(sql"""select 42 """.query[(Int)])
  }

  test("Test trivial2 operation"){
    check(sql"""select 42, 'foo' """.query[(Int, String)])
  }

  test("Test select person"){
    check(sql"""select id, name, age from person""".query[Person])
  }

  test("Test insert"){

    def insert1(name: String, age: Option[Short]): Update0 =
      sql"insert into person (name, age) values ($name, $age)".update

    insert1("Alice", Some(12)).run.transact(transactor).unsafeRunSync
    insert1("Bob", None).quick.unsafeRunSync

    val resultQuery: List[Person] = sql"""
          select
            id, name, age
          from person
    """.query[Person].stream.take(5).compile.toList.transact(transactor).unsafeRunSync()

    assert(resultQuery.size == 2)

  }

}
