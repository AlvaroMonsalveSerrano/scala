package es.ams.freemonaddoobie

import cats.{Id, ~>}
import cats.free.Free
import cats.free.Free.liftF
import cats.effect._
//import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts

object ExampleDoobieImpure extends App {

  type OperationDoobie[A] = Free[OperationDatabase, A]

  // Definición del dominio.-----------------------------------------
  case class Book(id: Long, name: String)

  // Definición de la gramática -------------------------------------
  sealed trait OperationDatabase[A]
  case class CreateSchema()     extends OperationDatabase[Unit]
  case class Insert(elem: Book) extends OperationDatabase[Int]
//  case class Select(key: Int) extends OperationDatabase[Option[String]]
  case class Select[T](key: Int) extends OperationDatabase[Option[T]]
  case class Delete(key: Int)    extends OperationDatabase[Unit]

  // Definición de las funciones del DSL ----------------------------
  def createSchema(): OperationDoobie[Unit] =
    liftF(CreateSchema())

  def insert(elem: Book): OperationDoobie[Int] =
    liftF[OperationDatabase, Int](Insert(elem))

  def select(key: Int): OperationDoobie[Option[String]] =
    liftF[OperationDatabase, Option[String]](Select(key))

  def delete(key: Int): OperationDoobie[Unit] =
    liftF[OperationDatabase, Unit](Delete(key))

  // Definición de un intérprete. -----------------------------------
  def impureInterpreter: OperationDatabase ~> Id = new (OperationDatabase ~> Id) {

    implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

    val xa = Transactor.fromDriverManager[IO](
      "com.mysql.jdbc.Driver",
      "jdbc:mysql://localhost:3306/doobie",
      "root",
      "root",
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

    override def apply[A](fa: OperationDatabase[A]): Id[A] = fa match {
      case CreateSchema() => {
        val dropBook = sql"""DROP TABLE IF EXISTS Book""".update.run

        val createBook =
          sql"""
          CREATE TABLE Book (
            id   SERIAL,
            name text
          )
        """.update.run

        val creator = for {
          _ <- dropBook
          _ <- createBook
        } yield ()

        println(s"getClass=${creator.getClass}")

        val result: Id[Unit] = creator.transact(xa).unsafeRunSync()
        result
      } // CreateSchema

      case Insert(elem) => {
        val result: Id[Int] = elem match {
          case p: Book =>
            val id   = p.id
            val name = p.name
            val resultInsert =
              sql"insert into Book (id, name) values ($id, $name)".update.run.transact(xa).unsafeRunSync()
            resultInsert
          case _ => 0
        }
        result
      } // Insert

      case Select(key) => {

        def findBookById(id: Int) = sql"""
              select name
              from Book
              where id = $key
          """.query[String]

        val listResult = findBookById(key).stream.compile.toList.transact(xa).unsafeRunSync()
        val result     = Some(listResult.head) // : Id[Option[Int]]
        result

      } // Select

      case Delete(key) => {

        def deleteById(id: Int) = sql"""
            delete from Book where id = $id
          """.update.run

        deleteById(key).transact(xa).unsafeRunSync()
        ()
      }

    } // apply

  } // impureInterpreter

  def programCreate: OperationDoobie[(Int, Option[String])] = for {
    _            <- createSchema()
    numInserted1 <- insert(Book(1, "Book1"))
    numInserted2 <- insert(Book(2, "Book2"))
    numInserted3 <- insert(Book(3, "Book3"))
    nameBook     <- select(2)
    _            <- delete(3)
  } yield {
    ((numInserted1 + numInserted2 + numInserted3), nameBook)
  }

  val resultProgramCreate = programCreate.foldMap(impureInterpreter)
  println(s"Result program create =${resultProgramCreate}")
  println()

  def programInser: OperationDoobie[Int] = for {
    numInserted4 <- insert(Book(4, "Book4"))
    numInserted5 <- insert(Book(5, "Book5"))
    numInserted6 <- insert(Book(6, "Book6"))
    _            <- delete(5)
  } yield {
    (numInserted4 + numInserted5 + numInserted6)
  }
  val resultProgramInser = programInser.foldMap(impureInterpreter)
  println(s"ProgramInser=${resultProgramInser}")
  println()

}
