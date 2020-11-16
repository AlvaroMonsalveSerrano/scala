package es.ams.freemonaddoobie

import cats.~>
import cats.free.Free
import cats.free.Free.liftF
import cats.data.State
import cats.effect.{Blocker, IO}
import doobie.Transactor
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.Aux

/**
  * Ejemplo básico de FreeMonad con un intérprete puro.
  *
  * Trabajar la generalidad.
  *
  */
object ExampleDoobiePure extends App {

  // Definición del dominio.-----------------------------------------
  case class Author(id: Long, name: String)

  // Definición de tipos.--------------------------------------------
  type Operation[A] = Free[OperationDB, A]

  type OperationState[A] = State[StateDatabase, A]

  type OperationDBResponse[A] = Either[Exception, A]
  type OperationDBResponseOption[A] = Either[Exception, Option[A]]

  // Definición de la gramática -------------------------------------
  sealed trait OperationDB[A]
  case class CreateSchema() extends OperationDB[OperationDBResponse[Boolean]]
  case class Insert(author: Author) extends OperationDB[OperationDBResponse[Int]]
  case class Select(key: Int) extends OperationDB[OperationDBResponseOption[String]]
  case class Delete(key: Int) extends OperationDB[OperationDBResponse[Int]]

  sealed trait StateDatabase
  case object Init extends StateDatabase
  case object Created extends StateDatabase
  case object Deleted extends StateDatabase

  // Definición de las funciones del DSL ----------------------------
  def createSchema(): Operation[OperationDBResponse[Boolean]] =
    liftF[OperationDB, OperationDBResponse[Boolean]](CreateSchema())

  def insert(elem: Author): Operation[OperationDBResponse[Int]] =
    liftF[OperationDB, OperationDBResponse[Int]](Insert(elem))

  def delete(key: Int): Operation[OperationDBResponse[Int]] =
    liftF[OperationDB, OperationDBResponse[Int]](Delete(key))

  def select(key: Int): Operation[OperationDBResponseOption[String]] =
    liftF[OperationDB, OperationDBResponseOption[String]](Select(key))

  // Definición del intérprete --------------------------------------
  def pureInterpreter: OperationDB ~> OperationState = new(OperationDB ~> OperationState){

    implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

    val xa = Transactor.fromDriverManager[IO](
      "com.mysql.jdbc.Driver",
      "jdbc:mysql://localhost:3306/doobie",
      "root",
      "root",
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

    override def apply[A](fa: OperationDB[A]): OperationState[A] = fa match {
      case CreateSchema() => {
        val resultCreateSchema: Either[Exception, Boolean] = MySqlDatabase.createSchemaIntoMySqlB(xa)
        resultCreateSchema
          .fold( ex => State[StateDatabase, A]{ state => (Init,  resultCreateSchema) },
            value => State[StateDatabase, A]{ state => (Created,  resultCreateSchema) } )

      }

      case Insert(author) =>{
        State[StateDatabase, A]{ state =>
          (Created,  MySqlDatabase.insertAuthorIntoMySql(xa, author))
        }
      }

      case Delete(key) => {
        State[StateDatabase, A]{ state =>
          (Created,  MySqlDatabase.deleteAuthorById(xa, key))
        }
      }

      case Select(key) => {
        State[StateDatabase, A]{ state =>
          (Created,  MySqlDatabase.selectAuthorById(xa, key))
        }
      }
    } // apply
  } // pureInterpreter


  def createDatabase(): Operation[Either[Exception, Boolean]] = for {
    result <- createSchema()
  } yield (result)

  val resultCreate = createDatabase().foldMap(pureInterpreter).run(Init).value
  println(s"Create database=${resultCreate}")
  println

  def insertAuthor(): Operation[Either[Exception, Int]] = for {
    num <- insert(Author(0, "Author1"))
  } yield (num)

  val resultInsertAuthor = insertAuthor().foldMap(pureInterpreter).run(Created).value
  println(s"Insert Author=${resultInsertAuthor}")
  println


  def deleteAuthor(): Operation[Either[Exception, Int]] = for {
    numInsert10 <- insert(Author(0, "Author10"))
    numInsert11 <- insert(Author(0, "Author11"))
    numInsert12 <- insert(Author(0, "Author12"))
    numDeleted <- delete(2)
  } yield (numDeleted)

  val resultDeleteAuthor = deleteAuthor().foldMap(pureInterpreter).run(Created).value
  println(s"Delete Author=${resultDeleteAuthor}")
  println


  def selectAuthorKO(): Operation[Either[Exception, Option[String]]] = for {
    author <- select(100)
  } yield (author)

  val resultSelectAuthorKO = selectAuthorKO.foldMap(pureInterpreter).run(Created).value
  println(s"Select Author=${resultSelectAuthorKO}")
  println


  def selectAuthorOK(): Operation[Either[Exception, Option[String]]] = for {
    author <- select(1)
  } yield (author)

  val resultSelectAuthorOK = selectAuthorOK.foldMap(pureInterpreter).run(Created).value
  println(s"Select Author=${resultSelectAuthorOK}")
  println

}

object MySqlDatabase{

  import es.ams.freemonaddoobie.ExampleDoobiePure.{Author, OperationDBResponse, OperationDBResponseOption}

  val dropAuthor: ConnectionIO[Int] = sql"""DROP TABLE IF EXISTS Author""".update.run
  val createAuthor: ConnectionIO[Int] = sql"""CREATE TABLE Author (id SERIAL, name text)""".update.run

  def createSchemaIntoMySqlA[A](xa: Aux[IO, Unit]): Either[Exception, Unit] = {
    val creator: ConnectionIO[Unit] = for {
      _ <- dropAuthor
      _ <- createAuthor
    } yield ()

    try{
      val resultDatabase: Unit = creator.transact(xa).unsafeRunSync
      Right(resultDatabase)

    } catch {
      case e: java.sql.SQLException =>{
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

    val creatorOperation = creator.attemptSql.map{
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
      sql"insert into Author (name) values ($name)"
        .update
        .withUniqueGeneratedKeys("id", "name")
        .attemptSql.map{
          case Right(value) => Right(1)
          case Left(exception) => Left(exception) // Error: existiendo la conexión, la operación falla.
        }

    try{
      insert.transact(xa).unsafeRunSync()
    } catch {
      case ex: Exception => Left(ex) // Error: No hay conexión, capturamos la excepción de error de conexión.
    }
  }


  def selectAuthorById(xa: Aux[IO, Unit], key: Int): OperationDBResponseOption[String] = {
      val selectOperation: Query0[String] = sql"""select name from Author where id = $key""".query[String]
      val selectResult =
        selectOperation
          .stream
          .compile
          .toList
          .attemptSql.map{
          case Right(value) => Right(Some(value.head))
          case Left(ex) => Left(ex)
           }

      try{
        selectResult.transact(xa).unsafeRunSync()
      } catch {
        case ex: Exception => Left(ex)
      }
  }


  def deleteAuthorById(xa: Aux[IO, Unit], key: Int): OperationDBResponse[Int] = {
    try{
      val deleteOperation: ConnectionIO[Int] = sql"""delete from Author where id = $key""".update.run
      Right(deleteOperation.transact(xa).unsafeRunSync())

    } catch {
      case ex: Exception =>
        Left(ex)
    }
  }
}
