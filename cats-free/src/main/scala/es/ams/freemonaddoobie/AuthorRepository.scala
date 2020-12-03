package es.ams.freemonaddoobie

import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor.Aux
import cats.effect.IO
import es.ams.freemonaddoobie.AuthorDSL.{OperationDBResponse, OperationDBResponseOption}

object AuthorRepository {

  val dropAuthor: ConnectionIO[Int]   = sql"""DROP TABLE IF EXISTS Author""".update.run
  val createAuthor: ConnectionIO[Int] = sql"""CREATE TABLE Author (id SERIAL, name text)""".update.run

  def createSchemaIntoMySqlA[A](xa: Aux[IO, Unit]): Either[Exception, Unit] = {
    val creator: ConnectionIO[Unit] = for {
      _ <- dropAuthor
      _ <- createAuthor
    } yield ()

    try {
      val resultDatabase: Unit = creator.transact(xa).unsafeRunSync()
      Right(resultDatabase)

    } catch {
      case e: java.sql.SQLException => {
        println(s"Error create schema: ${e}")
        Left(e)

      }
    }
  }

  def createSchemaIntoMySqlB[A](xa: Aux[IO, Unit]): OperationDBResponse[Boolean] = {
    val creator: ConnectionIO[Unit] = for {
      _ <- dropAuthor
      _ <- createAuthor
    } yield ()

    val creatorOperation = creator.attemptSql.map {
      case Right(value) =>
        Right(true)

      case Left(ex) =>
        Left(ex)
    }

    val transact = creatorOperation.transact(xa)
    try {
      transact.unsafeRunSync()

    } catch {
      case ex: Exception => Left(ex)
    }
  }

  def insertAuthorIntoMySql(xa: Aux[IO, Unit], author: Author): OperationDBResponse[Int] = {
    val name = author.name
    val insert =
      sql"insert into Author (name) values ($name)".update
        .withUniqueGeneratedKeys("id", "name")
        .attemptSql
        .map {
          case Right(value)    => Right(1)
          case Left(exception) => Left(exception) // Error: existiendo la conexión, la operación falla.
        }

    try {
      insert.transact(xa).unsafeRunSync()
    } catch {
      case ex: Exception => Left(ex) // Error: No hay conexión, capturamos la excepción de error de conexión.
    }
  }

  def selectAuthorById(xa: Aux[IO, Unit], key: Int): OperationDBResponseOption[String] = {
    val selectOperation: Query0[String] = sql"""select name from Author where id = $key""".query[String]
    val selectResult =
      selectOperation.stream.compile.toList.attemptSql.map {
        case Right(value) => Right(Some(value.head))
        case Left(ex)     => Left(ex)
      }

    try {
      selectResult.transact(xa).unsafeRunSync()
    } catch {
      case ex: Exception => Left(ex)
    }
  }

  def deleteAuthorById(xa: Aux[IO, Unit], key: Int): OperationDBResponse[Int] = {
    try {
      val deleteOperation: ConnectionIO[Int] = sql"""delete from Author where id = $key""".update.run
      Right(deleteOperation.transact(xa).unsafeRunSync())

    } catch {
      case ex: Exception =>
        Left(ex)
    }
  }
}
