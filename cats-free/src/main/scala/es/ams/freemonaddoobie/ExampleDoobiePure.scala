package es.ams.freemonaddoobie

import cats.{Id, ~>}
import cats.free.Free
import cats.free.Free.liftF
import cats.data.State
import cats.effect.{Blocker, IO}

import doobie.Transactor
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.Aux
import es.ams.freemonaddoobie.ExampleDoobiePure.Author

/**
  * Ejemplo básico de FreeMonad con un intérprete puro.
  *
  * Trabajar la generalidad.
  *
  */
object ExampleDoobiePure extends App {

  // Definición del dominio.-----------------------------------------
  case class Author(id: Long, name: String)

  type Operation[A] = Free[OperationDB, A]

  type OperationState[A] = State[StateDatabase, A]

  // Definición de la gramática -------------------------------------
  sealed trait OperationDB[A]
  case class CreateSchema() extends OperationDB[Unit]
  case class Insert(author: Author) extends OperationDB[Int]
  case class Select(key: Int) extends OperationDB[Option[String]]
  case class Delete(key: Int) extends OperationDB[Int]

  sealed trait StateDatabase
  case object Init extends StateDatabase
  case object Created extends StateDatabase
  case object Deleted extends StateDatabase

  // Definición de las funciones del DSL ----------------------------
  def createSchema(): Operation[Unit] =
    liftF(CreateSchema())

  def insert(elem: Author): Operation[Int] =
    liftF[OperationDB, Int](Insert(elem))

  def delete(key: Int): Operation[Int] =
    liftF[OperationDB, Int](Delete(key))

  def select(key: Int): Operation[Option[String]] =
    liftF[OperationDB, Option[String]](Select(key))

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
        val resultDatabase: Id[Unit] = MySqlDatabase.createSchemaIntoMySql(xa)
        val stateResult: OperationState[Unit] = State.pure[StateDatabase, Unit](resultDatabase)
        stateResult
      }

      case Insert(author) =>{
        val result: Id[Int] = MySqlDatabase.insertAuthorIntoMySql(xa, author)
        val stateResult: OperationState[Int] = State.pure[StateDatabase, Int](result)
        stateResult
      }

      case Delete(key) => {
        val result: Id[Int] = MySqlDatabase.deleteAuthorById(xa, key)
        val stateResult: OperationState[Int] = State.pure[StateDatabase, Int](result)
        stateResult
      }

      case Select(key) => {
        val result: Id[Option[String]] = MySqlDatabase.selectAuthorById(xa, key)
        val stateResult: OperationState[Option[String]] = State.pure[StateDatabase, Option[String]](result)
        stateResult
      }

    } // apply

  } // pureInterpreter


  def createDatabase(): Operation[Unit] = for {
    _ <- createSchema()
  } yield ()

  val resultCreate = createDatabase().foldMap(pureInterpreter).run(Init).value
  println(s"Create database=${resultCreate}")
  println

  def insertAuthor(): Operation[Int] = for {
    num <- insert(Author(1, "Author1"))
  } yield (num)

  val resultInsertAuthor = insertAuthor().foldMap(pureInterpreter).run(Created).value
  println(s"Insert Author=${resultInsertAuthor}")
  println


  def deleteAuthor(): Operation[Int] = for {
    numInsert10 <- insert(Author(10, "Author10"))
    numInsert11 <- insert(Author(11, "Author11"))
    numInsert12 <- insert(Author(12, "Author12"))
    numDeleted <- delete(11)
  } yield (numDeleted)

  val resultDeleteAuthor = deleteAuthor().foldMap(pureInterpreter).run(Created).value
  println(s"Delete Author=${resultDeleteAuthor}")
  println


  def selectAuthor(): Operation[Option[String]] = for {
    author <- select(10)
  } yield (author)

  val resultSelectAuthor = selectAuthor.foldMap(pureInterpreter).run(Created).value
  println(s"Select Author=${resultSelectAuthor}")
  println

}

object MySqlDatabase{

  def createSchemaIntoMySql[A](xa: Aux[IO, Unit]): Id[Unit] = {
    val dropAuthor: ConnectionIO[Int] = sql"""DROP TABLE IF EXISTS Author""".update.run
    val createAuthor: ConnectionIO[Int] = sql"""CREATE TABLE Author (id SERIAL, name text)""".update.run

    val creator = for {
      _ <- dropAuthor
      _ <- createAuthor
    } yield ()

    val resultDatabase: Id[Unit] = creator.transact(xa).unsafeRunSync
    resultDatabase
  }

  def insertAuthorIntoMySql(xa: Aux[IO, Unit], author: Author): Id[Int] = {
    val result: Id[Int] = author match {
      case p: Author =>
        val id = p.id
        val name = p.name
        val resultInsert = sql"insert into Author (id, name) values ($id, $name)".update.run.transact(xa).unsafeRunSync()
        resultInsert
      case _ => 0
    }
    result
  }

  def deleteAuthorById(xa: Aux[IO, Unit], key: Int): Id[Int] = {
    val deleteOperation: ConnectionIO[Int] = sql"""delete from Author where id = $key""".update.run
    val resultDelete: Int = deleteOperation.transact(xa).unsafeRunSync()
    resultDelete
  }

  def selectAuthorById(xa: Aux[IO, Unit], key: Int): Id[Option[String]] = {
    val selectOperation: Query0[String] = sql"""select name from Author where id = $key""".query[String]
    val listResult = selectOperation.stream.compile.toList.transact(xa).unsafeRunSync()
    val result = Some(listResult.head)
    result
  }

}
