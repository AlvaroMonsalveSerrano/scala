package es.ams.freemonaddoobie

import cats.effect.{Blocker, IO}
import cats.free.Free
import ciris.ConfigValue
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import doobie.util.transactor.Transactor.Aux
import es.ams.freemonaddoobie.AuthorComposingDSL._

import cats.implicits._

import ciris._

/**
  * Ejemplo de aplicación de composición de Free Monads.
  *
  * La funcionalidad que se realiza en el programa es muy básico. Es un ejemplo para anallizar el uso de las Free Monads.
  *
  * Ejemplo de variables de entorno: DDBB_HOST=localhost;DDBB_PORT=3306;DDBB_USER=root;DDBB_PWD=root;
  *
  *
  */
object ExampleDoobiePureComposing extends App{
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  final case class DatabaseConfig( host: String, port: String, user: String, password: Secret[String])

  def loadEnvironmentVariables(): DatabaseConfig = {
    val host: ConfigValue[String] = env("DDBB_HOST").or(prop("ddbb.host")).as[String]
    val port: ConfigValue[String] = env("DDBB_PORT").or(prop("ddbb.port")).as[String]
    val user: ConfigValue[String] = env("DDBB_USER").or(prop("ddbb.user")).as[String]
    val password: ConfigValue[Secret[String]] = env("DDBB_PWD").secret

    val configureEnv: ConfigValue[DatabaseConfig] = (host, port, user, password).parMapN(DatabaseConfig)

    configureEnv.load[IO].unsafeRunSync()
  }

  val dataConfigure: DatabaseConfig = loadEnvironmentVariables()

  private val xa = Transactor.fromDriverManager[IO](
      "com.mysql.jdbc.Driver",
      s"jdbc:mysql://${dataConfigure.host}:${dataConfigure.port}/doobie",
      s"${dataConfigure.user}",
      s"${dataConfigure.password.value}",
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  def programBusiness(xa: Aux[IO, Unit])(implicit DB: DBOperations[DoobiePureComposingApp],
                      L: LogOperations[DoobiePureComposingApp]): Free[DoobiePureComposingApp, Response[Option[String]]] = {

    import DB._
    import L._

    for {
      _ <- debugLog("Configuring database...")
      _ <- configure(xa)
      _ <- debugLog("Creating database...")
      result <- createSchema()
      numInsert1 <- insert(Author(0, "AuthorTest1"))
      numInsert2 <- insert(Author(0, "AuthorTest2"))
      numDelete1 <- delete(key = 1)
      nameAuthor <- select(key = 2)
      _ <- infoLog("Created Database.")
    } yield {
      nameAuthor
    }

  }

  val result = programBusiness(xa).foldMap(interpreter)

  println(s"Result=${result}")
  println()

}
