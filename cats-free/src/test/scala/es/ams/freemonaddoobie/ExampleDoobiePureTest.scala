package es.ams.freemonaddoobie

import cats.effect.{Blocker, IO}
import doobie.Transactor
import doobie.util.ExecutionContexts

import es.ams.freemonaddoobie.AuthorDSL._

import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.ExecutionContext

/**
  * Se necesita una base de datos MySQL en un contenedor para realizar las pruebas.
  *
  * Comando SQL para la creación de una base de datos:
  *   CREATE DATABASE IF NOT EXISTS doobieTest;
  */
class ExampleDoobiePureTest extends AnyFlatSpec {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  val xa = Transactor.fromDriverManager[IO](
    "com.mysql.jdbc.Driver",
    "jdbc:mysql://localhost:3306/doobieTest",
    "root",
    "root",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  "Interprete Pure" should "CreateSchema test" in {

    val test = for{
      result <- createSchema()
    } yield (result)

    val resultTest: (StateDatabase, OperationDBResponse[Boolean]) = test.foldMap(pureInterpreter(xa)).run(Init).value
    assert(resultTest._1 === Created)
    assert(resultTest._2 === Right(true))

  }

  it should "Insert Author test" in {
    val test = for {
      create <- createSchema()
      num <- insert( Author(0, "Author1"))
    } yield (num)

    val resultTest: (StateDatabase, OperationDBResponse[Int]) = test.foldMap(pureInterpreter(xa)).run(Created).value
    assert(resultTest._1 === Created)
    assert(resultTest._2 === Right(1))
  }

  it should "Delete Author test" in {
    val test = for {
      create <- createSchema()
      num <- insert(Author(0, "Author1"))
      numDeleted <- delete(1)

    } yield {numDeleted}

    val resultTest: (StateDatabase, OperationDBResponse[Int]) = test.foldMap(pureInterpreter(xa)).run(Created).value
    assert(resultTest._1 === Created)
    assert(resultTest._2 === Right(1))

  }

  it should "Select Author test." in {
    val nameAuthor = "AuthorTest"
    val test = for {
      create <- createSchema()
      num <- insert(Author(0, nameAuthor))
      author <- select(1)

    } yield {author}

    val resultTest: (StateDatabase, OperationDBResponseOption[String]) = test.foldMap(pureInterpreter(xa)).run(Created).value
    assert(resultTest._1 === Created)
    assert(resultTest._2 === Right(Some(nameAuthor)))

  }

}
