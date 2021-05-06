package es.ams.modulelayer

import zio.clock.Clock
import zio.{Has, IO, Runtime, UIO, URIO, ZIO, ZLayer}
import zio.console.Console
import zio.random.Random

/** Reference: https://zio.dev/docs/howto/howto_use_layers
  *            https://zio.dev/docs/howto/use-modules-and-layers
  */
object ModuleLayerExample1 extends App {

  // Module UserRepo -----------------------------------------
  type UserId   = Int
  type DBError  = String
  type UserRepo = Has[UserRepo.Service]

  case class User(id: UserId, name: String)

  object UserRepo {
    trait Service {
      def getUser(userId: UserId): IO[DBError, Option[User]]
      def createUser(user: User): IO[DBError, Unit]
    }

    // Service implementation
    val inMemory: ZLayer[Any, Nothing, UserRepo] = ZLayer.succeed(
      new Service {
        override def getUser(userId: UserId): IO[DBError, Option[User]] =
          IO.succeed(Some(User(id = userId, name = "nameTest")))

        override def createUser(user: User): IO[DBError, Unit] = IO.succeed(println(s"User created...${user}"))
      }
    )

    // Accesor Method
    def getUser(userId: UserId): ZIO[UserRepo, DBError, Option[User]] = ZIO.accessM(_.get.getUser(userId))

    def createUser(user: User): ZIO[UserRepo, DBError, Unit] = ZIO.accessM(_.get.createUser(user))
  }

  // Module Logging  -----------------------------------------

  type Logging = Has[Logging.Service]
  object Logging {
    trait Service {
      def info(s: String): UIO[Unit]
      def error(s: String): UIO[Unit]
    }

    // Service implementation
    val consoleLogger: ZLayer[Console, Nothing, Logging] = ZLayer.fromFunction(console =>
      new Service {
        def info(s: String): UIO[Unit] = console.get.putStrLn(s"info - ${s}")

        def error(s: String): UIO[Unit] = console.get.putStrLn(s"error - ${s}")
      }
    )

    // Accesor Method
    def info(s: String): URIO[Logging, Unit] = ZIO.accessM(_.get.info(s))

    def error(s: String): URIO[Logging, Unit] = ZIO.accessM(_.get.error(s))
  }

  // Composición horizontal de capas. Construye una capa que tiene los requerimientos de ambas.
  val horizontal: ZLayer[Console, Nothing, Logging with UserRepo] = Logging.consoleLogger ++ UserRepo.inMemory

  // Composición vertical de capas. La salida de la primera capa es la entrada de la segunda.
  val fullLayer: ZLayer[Any, Nothing, Logging with UserRepo] = Console.live >>> horizontal

  def executeProgram1(): Unit = {
    println(s"-*-*- Program1: Basic define -*-*-")
    val user = User(id = 1, name = "nameTest")
    val programMakerUser: ZIO[Logging with UserRepo, DBError, Unit] = for {
      _ <- Logging.info(s"Inserting user...")
      _ <- UserRepo.createUser(user)
      _ <- Logging.info(s"User inserted.")
    } yield ()

    Runtime.default.unsafeRun(programMakerUser.provideLayer(fullLayer))

  }

  /** Providing partial environments
    */
  def executeProgram2(): Unit = {
    println(s"-*-*- Program2: Providing partial environments -*-*-")
    val programMakerUSer2: ZIO[Logging with UserRepo with Clock with Random, DBError, Unit] = for {
      uid       <- zio.random.nextInt.map(elem => elem + 1)
      createdAt <- zio.clock.currentDateTime.orDie
      _         <- Logging.info(s"Inserting user.")
      _         <- UserRepo.createUser(User(uid, "Test2"))
      _         <- Logging.info(s"Inserted User, created at $createdAt.")
    } yield ()

    // ZIO[ZEnv, DBError, Unit]
    Runtime.default.unsafeRun(programMakerUSer2.provideCustomLayer(fullLayer))

  }

  /** Updating local dependencies
    */
  def executeProgram3(): Unit = {
    println(s"-*-*- Program3: Updating local dependencies -*-*-")
    val withNoSQLDatabase = horizontal.update[UserRepo.Service] { oldRepo =>
      new UserRepo.Service {
        override def getUser(userId: UserId): IO[DBError, Option[User]] =
          IO.succeed(Some(User(id = userId, name = "nameTest-NoSQL")))

        override def createUser(user: User): IO[DBError, Unit] =
          IO.succeed(println(s"User created in No SQL ...${user}"))
      }
    }

    // Composición horizontal de capas. Construye una capa que tiene los requerimientos de ambas.
    val horizontalNoSQL: ZLayer[Console, Nothing, Logging with UserRepo] = Logging.consoleLogger ++ withNoSQLDatabase

    // Composición vertical de capas. La salida de la primera capa es la entrada de la segunda.
    val fullLayerNoSQL: ZLayer[Any, Nothing, Logging with UserRepo] = Console.live >>> horizontalNoSQL

    val user = User(id = 1, name = "nameTestNoSQL")
    val programMakerUserNoSQL: ZIO[Logging with UserRepo, DBError, Unit] = for {
      _ <- Logging.info(s"Inserting user...")
      _ <- UserRepo.createUser(user)
      _ <- Logging.info(s"User inserted.")
    } yield ()

    Runtime.default.unsafeRun(programMakerUserNoSQL.provideLayer(fullLayerNoSQL))

  }

  executeProgram1()
  executeProgram2()
  executeProgram3()

}
