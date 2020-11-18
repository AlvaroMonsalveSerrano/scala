package es.ams.freemonaddoobie

import cats.data.{EitherK}
import cats.effect.{Blocker, IO}
import cats.free.Free
import cats.{Id, InjectK, ~>}

import doobie.util.ExecutionContexts
import doobie.Transactor

/**
  * Ejemplo de compasición de Free Monads.
  *
  * AuthorComposingDSL define dos DSL: DBOperations, para las operaciones con la base de datos; y, LogOperations, para
  * las trazas de log.
  *
  */
object AuthorComposingDSL {

  type DoobiePureComposingApp[A] = EitherK[DBOperation, LogOperation, A]

  type OperationResponse[A] = Id[A]

  // Definición de la gramática -------------------------------------
  sealed trait DBOperation[A]
  case class CreateSchema() extends DBOperation[Either[Exception, Boolean]]
  case class Insert(author: Author) extends DBOperation[Either[Exception, Int]]
  case class Select(key: Int) extends DBOperation[Either[Exception, Option[String]]]
  case class Delete(key: Int) extends DBOperation[Either[Exception, Int]]

  // Definición de la gramática -------------------------------------
  sealed trait LogOperation[A]
  case class Info(msg: String) extends LogOperation[Either[Exception, Unit]]
  case class Debug(msg: String) extends LogOperation[Either[Exception, Unit]]

  // Definición del DSL DBOperations ---------------------------------
  class DBOperations[F[_]](implicit I: InjectK[DBOperation, F]){
    def createSchema(): Free[F, Either[Exception, Boolean]] = Free.inject[DBOperation, F](CreateSchema())

    def insert(elem: Author): Free[F, Either[Exception, Int]] = Free.inject[DBOperation, F](Insert(elem))

    def delete(key: Int): Free[F, Either[Exception, Int]] = Free.inject[DBOperation, F](Delete(key))

    def select(key: Int): Free[F, Either[Exception, Option[String]]] = Free.inject[DBOperation, F](Select(key))
  }
  object DBOperations{
    implicit def dboperations[F[_]](implicit I: InjectK[DBOperation, F]) = new DBOperations[F]()
  }


  class LogOperations[F[_]](implicit I: InjectK[LogOperation, F]){
      def infoLog(msg: String): Free[F, Either[Exception, Unit]] = Free.inject[LogOperation, F](Info(msg))

      def debugLog(msg: String): Free[F, Either[Exception, Unit]] = Free.inject[LogOperation, F](Debug(msg))
  }
  object LogOperations{
    implicit def logopearations[F[_]](implicit I: InjectK[LogOperation, F]) = new LogOperations[F]()
  }


  // Definición del intérprete --------------------------------------
  object DBOperationsInterpreter extends (DBOperation ~> OperationResponse){

    implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

    val xa = Transactor.fromDriverManager[IO](
      "com.mysql.jdbc.Driver",
      "jdbc:mysql://localhost:3306/doobie",
      "root",
      "root",
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

    override def apply[A](fa: DBOperation[A]) = fa match {

      case CreateSchema() => {
        val resultCreateSchema: Either[Exception, Boolean] = AuthorRepository.createSchemaIntoMySqlB(xa)
        val result: OperationResponse[ Either[Exception, Boolean]] = resultCreateSchema
        result
      }

      case Insert(author) =>{
        val resultTask: Either[Exception, Int] = AuthorRepository.insertAuthorIntoMySql(xa, author)
        val result: OperationResponse[Either[Exception, Int]] = resultTask
        result

      }

      case Delete(key) => {
        val resultTask: Either[Exception, Int] = AuthorRepository.deleteAuthorById(xa, key)
        val result: OperationResponse[Either[Exception, Int]] = resultTask
        (result)
      }

      case Select(key) => {
        val resultTask: Either[Exception, Option[String]] = AuthorRepository.selectAuthorById(xa, key)
        val result: OperationResponse[ Either[Exception, Option[String]]] = resultTask
        (result)
      }

    }

  }


  object LogOperationsInterpreter extends (LogOperation ~> OperationResponse){

    override def apply[A](fa: LogOperation[A]) = fa match {

        case Info(msg) =>
          println(s"[*** INFO] ${msg}")
          val result: OperationResponse[Either[Exception, Unit]] = Right(Unit)
          result

        case Debug(msg) =>
          println(s"[*** DEBUG] ${msg}")
          val result: OperationResponse[Either[Exception, Unit]] = Right(Unit)
          result
    }

  }

  val interpreter: DoobiePureComposingApp ~> OperationResponse = DBOperationsInterpreter or LogOperationsInterpreter


}
