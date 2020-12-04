package es.ams.freemonaddoobie

import cats.effect.{Blocker, IO, Resource}
import doobie.h2.H2Transactor
import doobie.util.ExecutionContexts
import es.ams.freemonaddoobie.AuthorDSL._

/** Ejemplo básico de FreeMonad con un intérprete puro.
  *
  * Trabajar la generalidad.
  */
object ExampleDoobiePure extends App {

  // Definición del transactor a la BBDD. ---------------------------
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
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

  // Definición de programas de prueba. -----------------------------
  def createDatabase(): Operation[Either[Exception, Boolean]] = for {
    result <- createSchema()
  } yield (result)

  val resultCreate: IO[(StateDatabase, Either[Exception, Boolean])] = transactor.use { xa =>
    val resultCreate = createDatabase().foldMap(pureInterpreter(xa)).run(Init).value
    IO(resultCreate)

  }
  val resultCreateRun = resultCreate.unsafeRunSync()
  println(s"Create database=${resultCreateRun}")
  println()

  def insertAuthor(): Operation[Either[Exception, Int]] = for {
    num <- insert(Author(0, "Author1"))
  } yield (num)

  val resultInsert: IO[(StateDatabase, Either[Exception, Int])] = transactor.use { xa =>
    IO(insertAuthor().foldMap(pureInterpreter(xa)).run(Created).value)
  }
  val resultInsertRun = resultInsert.unsafeRunSync()
  println(s"Insert Author=${resultInsertRun}")
  println()

  def deleteAuthor(): Operation[Either[Exception, Int]] = for {
    numInsert10 <- insert(Author(0, "Author10"))
    numInsert11 <- insert(Author(0, "Author11"))
    numInsert12 <- insert(Author(0, "Author12"))
    numDeleted  <- delete(2)
  } yield (numDeleted)

  val resultDelete: IO[(StateDatabase, Either[Exception, Int])] = transactor.use { xa =>
    IO(deleteAuthor().foldMap(pureInterpreter(xa)).run(Created).value)
  }
  val resultDeletetRun = resultDelete.unsafeRunSync()
  println(s"Delete Author=${resultDeletetRun}")
  println()

  def selectAuthorKO(): Operation[Either[Exception, Option[String]]] = for {
    author <- select(100)
  } yield (author)

  val resultSelectAuthorKO: IO[(StateDatabase, Either[Exception, Option[String]])] = transactor.use { xa =>
    IO(selectAuthorKO().foldMap(pureInterpreter(xa)).run(Created).value)
  }
  val resultSelectAuthorKORun = resultSelectAuthorKO.unsafeRunSync()
  println(s"Select Author=${resultSelectAuthorKORun}")
  println()

  def selectAuthorOK(): Operation[Either[Exception, Option[String]]] = for {
    author <- select(1)
  } yield (author)

  val resultSelectAuthorOK: IO[(StateDatabase, Either[Exception, Option[String]])] = transactor.use { xa =>
    IO(selectAuthorOK().foldMap(pureInterpreter(xa)).run(Created).value)
  }
  val resultSelectAuthorOKRun = resultSelectAuthorOK.unsafeRunSync()
  println(s"Select Author=${resultSelectAuthorOKRun}")
  println()

}
