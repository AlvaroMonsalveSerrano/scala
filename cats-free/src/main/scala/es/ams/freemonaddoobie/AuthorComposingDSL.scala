package es.ams.freemonaddoobie

import cats.data.EitherK
import cats.effect.{Blocker, IO, Resource}
import cats.free.Free
import cats.{Id, InjectK, ~>}
import ciris.{ConfigValue, Secret, env, prop}
import doobie.util.ExecutionContexts
import doobie.h2.H2Transactor
import doobie.util.transactor.Transactor.Aux

import cats.implicits._

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

    final case class DatabaseConfig(host: String, port: String, user: String, password: Secret[String])

    /** Ejemplo de variables de entorno para una base de datos H2:
      * DDBB_HOST=mem:test;DDBB_PORT=;DDBB_USER=sa;DDBB_PWD=
      */
    def loadEnvironmentVariables(): DatabaseConfig = {
      val host: ConfigValue[String]             = env("DDBB_HOST").or(prop("ddbb.host")).as[String].default("mem:test")
      val port: ConfigValue[String]             = env("DDBB_PORT").or(prop("ddbb.port")).as[String].default("")
      val user: ConfigValue[String]             = env("DDBB_USER").or(prop("ddbb.user")).as[String].default("sa")
      val password: ConfigValue[Secret[String]] = env("DDBB_PWD").secret.default(Secret.apply(""))

      val configureEnv: ConfigValue[DatabaseConfig] = (host, port, user, password).parMapN(DatabaseConfig)

      configureEnv.load[IO].unsafeRunSync()
    }

    val configDatabase = loadEnvironmentVariables()
    val transactor: Resource[IO, H2Transactor[IO]] =
      for {
        ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
        be <- Blocker[IO] // our blocking EC
        xa <- H2Transactor.newH2Transactor[IO](
          s"jdbc:h2:${configDatabase.host};DB_CLOSE_DELAY=-1", // connect URL
          s"${configDatabase.user}",                           // username
          s"${configDatabase.password.value}",                 // password
          ce,                                                  // await connection here
          be                                                   // execute JDBC operations here
        )
      } yield xa

    override def apply[A](fa: DBOperation[A]) = fa match {

      case Configure(xaTransactor) => {
        val result: OperationResponse[Response[Unit]] = Right(())
        result

      }

      case CreateSchema() => {

        val resultCreate: IO[OperationResponse[Response[Boolean]]] = transactor.use { xa =>
          val resultCreateSchema: Response[Boolean] = AuthorRepository.createSchemaIntoMySqlB(xa)
          IO(resultCreateSchema)
        }
        resultCreate.unsafeRunSync()

      }

      case Insert(author) => {

        val resultInsert: IO[OperationResponse[Response[Int]]] = transactor.use { xa =>
          val resultTask: Response[Int] = AuthorRepository.insertAuthorIntoMySql(xa, author)
          IO(resultTask)
        }
        resultInsert.unsafeRunSync()
      }

      case Delete(key) => {
        val resultDelete: IO[OperationResponse[Response[Int]]] = transactor.use { xa =>
          val resultTask: Response[Int] = AuthorRepository.deleteAuthorById(xa, key)
          IO(resultTask)
        }
        resultDelete.unsafeRunSync()
      }

      case Select(key) => {
        val resultSelect: IO[OperationResponse[Response[Option[String]]]] = transactor.use { xa =>
          val resultTask: Response[Option[String]] = AuthorRepository.selectAuthorById(xa, key)
          IO(resultTask)
        }
        resultSelect.unsafeRunSync()
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
