package es.ams.freemonaddoobie

import cats.effect.{Blocker, IO}
import doobie.Transactor
import doobie.util.ExecutionContexts

import es.ams.freemonaddoobie.AuthorDSL._

/**
  * Ejemplo básico de FreeMonad con un intérprete puro.
  *
  * Trabajar la generalidad.
  *
  */
object ExampleDoobiePure extends App {

  // Definición del transactor a la BBDD. ---------------------------
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  val xa = Transactor.fromDriverManager[IO](
    "com.mysql.jdbc.Driver",
    "jdbc:mysql://localhost:3306/doobie",
    "root",
    "root",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  // Definición de programas de prueba. -----------------------------
  def createDatabase(): Operation[Either[Exception, Boolean]] = for {
    result <- createSchema()
  } yield (result)

  val resultCreate = createDatabase().foldMap(pureInterpreter(xa)).run(Init).value
  println(s"Create database=${resultCreate}")
  println()

  def insertAuthor(): Operation[Either[Exception, Int]] = for {
    num <- insert(Author(0, "Author1"))
  } yield (num)

  val resultInsertAuthor = insertAuthor().foldMap(pureInterpreter(xa)).run(Created).value
  println(s"Insert Author=${resultInsertAuthor}")
  println()


  def deleteAuthor(): Operation[Either[Exception, Int]] = for {
    numInsert10 <- insert(Author(0, "Author10"))
    numInsert11 <- insert(Author(0, "Author11"))
    numInsert12 <- insert(Author(0, "Author12"))
    numDeleted <- delete(2)
  } yield (numDeleted)

  val resultDeleteAuthor = deleteAuthor().foldMap(pureInterpreter(xa)).run(Created).value
  println(s"Delete Author=${resultDeleteAuthor}")
  println()


  def selectAuthorKO(): Operation[Either[Exception, Option[String]]] = for {
    author <- select(100)
  } yield (author)

  val resultSelectAuthorKO = selectAuthorKO().foldMap(pureInterpreter(xa)).run(Created).value
  println(s"Select Author=${resultSelectAuthorKO}")
  println()


  def selectAuthorOK(): Operation[Either[Exception, Option[String]]] = for {
    author <- select(1)
  } yield (author)

  val resultSelectAuthorOK = selectAuthorOK().foldMap(pureInterpreter(xa)).run(Created).value
  println(s"Select Author=${resultSelectAuthorOK}")
  println()

}

