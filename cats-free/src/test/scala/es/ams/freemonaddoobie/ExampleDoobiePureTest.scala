package es.ams.freemonaddoobie

import cats.effect.{Blocker, IO, Resource}
import doobie.h2.H2Transactor
import doobie.util.ExecutionContexts
import es.ams.freemonaddoobie.AuthorDSL._
import org.scalatest.flatspec.AnyFlatSpec

import scala.concurrent.ExecutionContext

/** Se necesita una base de datos MySQL en un contenedor para realizar las pruebas.
  *
  * Comando SQL para la creación de una base de datos:
  *   CREATE DATABASE IF NOT EXISTS doobieTest;
  */
class ExampleDoobiePureTest extends AnyFlatSpec {

  implicit val cs = IO.contextShift(ExecutionContext.global)
  val transactor: Resource[IO, H2Transactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      be <- Blocker[IO] // our blocking EC
      xa <- H2Transactor.newH2Transactor[IO](
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", // connect URL
        "sa",                                 // username
        "",                                   // password
        ce,                                   // await connection here
        be                                    // execute JDBC operations here
      )
    } yield xa

  "Interprete Pure" should "CreateSchema test" in {

    val test = for {
      result <- createSchema()
    } yield (result)

    val resultCreate: IO[(StateDatabase, OperationDBResponse[Boolean])] = transactor.use { xa =>
      IO(test.foldMap(pureInterpreter(xa)).run(Init).value)
    }

    val resultCreateRun = resultCreate.unsafeRunSync()
    assert(resultCreateRun._1 === Created)
    assert(resultCreateRun._2 === Right(true))

  }

  it should "Insert Author test" in {
    val test = for {
      create <- createSchema()
      num    <- insert(Author(0, "Author1"))
    } yield (num)

    val resultCreate: IO[(StateDatabase, OperationDBResponse[Int])] = transactor.use { xa =>
      IO(test.foldMap(pureInterpreter(xa)).run(Created).value)
    }

    val resultCreateRun = resultCreate.unsafeRunSync()
    assert(resultCreateRun._1 === Created)
    assert(resultCreateRun._2 === Right(1))
  }

  it should "Delete Author test" in {
    val test = for {
      create     <- createSchema()
      num        <- insert(Author(0, "Author1"))
      numDeleted <- delete(1)

    } yield { numDeleted }

    val resultCreate: IO[(StateDatabase, OperationDBResponse[Int])] = transactor.use { xa =>
      IO(test.foldMap(pureInterpreter(xa)).run(Created).value)
    }

    val resultCreateRun = resultCreate.unsafeRunSync()
    assert(resultCreateRun._1 === Created)
    assert(resultCreateRun._2 === Right(1))

  }

  it should "Select Author test." in {
    val nameAuthor = "AuthorTest"
    val test = for {
      create <- createSchema()
      num    <- insert(Author(0, nameAuthor))
      author <- select(1)

    } yield { author }

    val resultCreate: IO[(StateDatabase, OperationDBResponseOption[String])] = transactor.use { xa =>
      IO(test.foldMap(pureInterpreter(xa)).run(Created).value)
    }

    val resultCreateRun = resultCreate.unsafeRunSync()
    assert(resultCreateRun._1 === Created)
    assert(resultCreateRun._2 === Right(Some(nameAuthor)))

  }

}
