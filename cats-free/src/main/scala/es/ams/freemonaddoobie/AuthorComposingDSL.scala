package es.ams.freemonaddoobie

import cats.data.EitherK
import cats.effect.{Blocker, IO}
import cats.free.Free
import cats.{Id, InjectK, ~>}
import doobie.util.ExecutionContexts
import doobie.Transactor
import doobie.util.transactor.Transactor.Aux

/** Ejemplo de composición de Free Monads.
  *
  * AuthorComposingDSL define dos DSL: DBOperations, para las operaciones con la base de datos; y, LogOperations, para
  * las trazas de log.
  *
  * Ejemplo de variables de entorno: DDBB_HOST=localhost;DDBB_PORT=3306;DDBB_USER=root;DDBB_PWD=root;
  */
object AuthorComposingDSL {

  val interpreter: DoobiePureComposingApp ~> OperationResponse = DBOperationsInterpreter or LogOperationsInterpreter

  type DoobiePureComposingApp[A] = EitherK[DBOperation, LogOperation, A]

  type Response[A] = Either[Exception, A]

  type OperationResponse[A] = Id[A]

  // Definición de la gramática -------------------------------------
  sealed trait DBOperation[A]
  case class Configure(xa: Aux[IO, Unit]) extends DBOperation[Response[Unit]]
  case class CreateSchema()               extends DBOperation[Response[Boolean]]
  case class Insert(author: Author)       extends DBOperation[Response[Int]]
  case class Select(key: Int)             extends DBOperation[Response[Option[String]]]
  case class Delete(key: Int)             extends DBOperation[Response[Int]]

  // Definición de la gramática -------------------------------------
  sealed trait LogOperation[A]
  case class Info(msg: String)  extends LogOperation[Response[Unit]]
  case class Debug(msg: String) extends LogOperation[Response[Unit]]

  // Definición del DSL DBOperations ---------------------------------
  class DBOperations[F[_]](implicit I: InjectK[DBOperation, F]) {

    def configure(xa: Aux[IO, Unit]): Free[F, Response[Unit]] = Free.inject[DBOperation, F](Configure(xa))

    def createSchema(): Free[F, Response[Boolean]] = Free.inject[DBOperation, F](CreateSchema())

    def insert(elem: Author): Free[F, Response[Int]] = Free.inject[DBOperation, F](Insert(elem))

    def delete(key: Int): Free[F, Response[Int]] = Free.inject[DBOperation, F](Delete(key))

    def select(key: Int): Free[F, Response[Option[String]]] = Free.inject[DBOperation, F](Select(key))
  }
  object DBOperations {
    implicit def dboperations[F[_]](implicit I: InjectK[DBOperation, F]) = new DBOperations[F]()
  }

  class LogOperations[F[_]](implicit I: InjectK[LogOperation, F]) {
    def infoLog(msg: String): Free[F, Response[Unit]] = Free.inject[LogOperation, F](Info(msg))

    def debugLog(msg: String): Free[F, Response[Unit]] = Free.inject[LogOperation, F](Debug(msg))
  }
  object LogOperations {
    implicit def logopearations[F[_]](implicit I: InjectK[LogOperation, F]) = new LogOperations[F]()
  }

  // Definición del intérprete --------------------------------------
  object DBOperationsInterpreter extends (DBOperation ~> OperationResponse) {

    implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

    // TODO var -> val?
    private var xa = Transactor.fromDriverManager[IO](
      s"com.mysql.jdbc.Driver",
      s"jdbc:mysql://host:port/doobie",
      s"user",
      s"pwd",
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

    override def apply[A](fa: DBOperation[A]) = fa match {

      case Configure(xaTransactor) => {
        this.xa = xaTransactor
        val result: OperationResponse[Response[Unit]] = Right(())
        result

      }

      case CreateSchema() => {
        val resultCreateSchema: Response[Boolean]        = AuthorRepository.createSchemaIntoMySqlB(xa)
        val result: OperationResponse[Response[Boolean]] = resultCreateSchema
        result
      }

      case Insert(author) => {
        val resultTask: Response[Int]                = AuthorRepository.insertAuthorIntoMySql(xa, author)
        val result: OperationResponse[Response[Int]] = resultTask
        result

      }

      case Delete(key) => {
        val resultTask: Response[Int]                = AuthorRepository.deleteAuthorById(xa, key)
        val result: OperationResponse[Response[Int]] = resultTask
        (result)
      }

      case Select(key) => {
        val resultTask: Response[Option[String]]                = AuthorRepository.selectAuthorById(xa, key)
        val result: OperationResponse[Response[Option[String]]] = resultTask
        (result)
      }

    }

  }

  object LogOperationsInterpreter extends (LogOperation ~> OperationResponse) {

    override def apply[A](fa: LogOperation[A]) = fa match {

      case Info(msg) =>
        println(s"[*** INFO] ${msg}")
        val result: OperationResponse[Response[Unit]] = Right(())
        result

      case Debug(msg) =>
        println(s"[*** DEBUG] ${msg}")
        val result: OperationResponse[Response[Unit]] = Right(())
        result
    }

  }

}
