package es.doobie.ejemdoc

/**
 *
 * Connection to Database
 * Reference: https://tpolecat.github.io/doobie/docs/03-Connecting.html
 *
 * Database H2
 */
object Example1ConnectionDatabaseDoobie extends App{

  import doobie._
  import doobie.implicits._

  import cats.effect.IO
//  import cats._
  import cats.effect._
  import cats.implicits._
  import scala.concurrent.ExecutionContext


  def example1(): Unit = {
    implicit val cs = IO.contextShift(ExecutionContext.global)

    val xa = Transactor.fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:mem:testdb",
      "sa",
      "password"
      //    , Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

    val program1 = 42.pure[ConnectionIO]
    val io = program1.transact(xa)
    println(s"program1 = ${io.unsafeRunSync()}")
    println()

  }


  def example2(): Unit = {

    implicit val cs = IO.contextShift(ExecutionContext.global)

    val xa = Transactor.fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:mem:testdb",
      "sa",
      "password"
      //    , Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

    // Conexión a una base de datos para la consulta de una constante.
    val program2 = sql"select 42".query[Int].unique
    val io = program2.transact(xa)
    println(s"program2 = ${io.unsafeRunSync()}")
    println()

  }

  def example3(): Unit = {

    implicit val cs = IO.contextShift(ExecutionContext.global)

    val xa = Transactor.fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:mem:testdb",
      "sa",
      "password"
    )

    // Conexión a una base de datos para la consulta de una constante.
    val program3: ConnectionIO[(Int, Double)] =
      for{
        a <- sql"select 42".query[Int].unique
        b <- sql"select random()".query[Double].unique
      }yield (a, b)

    val io = program3.transact(xa)
    println(s"program3 = ${io.unsafeRunSync()}")
    println()


    // Applicative functor
    val program3a = {
      val a: ConnectionIO[Int] = sql"select 42".query[Int].unique
      val b: ConnectionIO[Double] = sql"select random()".query[Double].unique
      (a, b).tupled
    }
    val ioa = program3a.transact(xa)
    println(s"program3a = ${ioa.unsafeRunSync()}")
    println()

    val valueList = program3a.replicateA(5)
    println(s"valueList=${valueList}")

    val result = valueList.transact(xa)
    println(s"result=${result}")

    result.unsafeRunSync().foreach(println)

  }


  def example4(): Unit = {
    import cats.effect.Blocker

    val dbExecutionContext = ExecutionContext.global
    implicit val contextShift: ContextShift[IO] = IO.contextShift(dbExecutionContext)

    // El interprete transforma un ConnectionIO[A] program into a Kleisli[IO, Connection, A]
    val interpreter = KleisliInterpreter[IO](Blocker.liftExecutionContext(ExecutionContexts.synchronous)).ConnectionInterpreter

    val program1 = 42.pure[ConnectionIO]
    val kleisli = program1.foldMap(interpreter)

    val io3 = IO(null: java.sql.Connection) >>= kleisli.run

    println(s"example4=${io3.unsafeRunSync()}")
  }


  example1()
  example2()
  example3()
  example4()


}
