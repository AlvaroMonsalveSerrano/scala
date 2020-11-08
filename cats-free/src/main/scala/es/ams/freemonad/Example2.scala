package es.ams.freemonad

import cats.data.EitherK
import cats.free.Free
import cats.{Id, InjectK, ~>}
import scala.collection.mutable.ListBuffer

/**
  * FreeMomnad (Cont.)
  * ----------
  *
  * https://typelevel.org/cats/datatypes/freemonad.html
  *
  * Composición de Free Monad.
  *
  * Las aplicaciones del mundo real a menudo combinan diferentes álgebras. La clase de inyección descrita por Swierdtra
  * nos permite componer álgebras en el contexto de Free.
  *
  * Este es un ejemplo trivial.
  *
  */

object Example2 extends App {

  type CatsApp[A] = EitherK[DataOp, Interact, A]

  //
  // Definición de la gramática.
  //
  sealed trait Interact[A]
  case class Ask(propmt: String) extends Interact[String]
  case class Tell(msg: String) extends Interact[Unit]

  sealed trait DataOp[A]
  case class AddCat(a: String) extends DataOp[Unit]
  case class GetAllCats() extends DataOp[List[String]]

  //
  // Definición del DSL
  //
  class Interacts[F[_]](implicit I: InjectK[Interact, F]){
    def tell(msg: String): Free[F, Unit] = Free.inject[Interact, F](Tell(msg))
    def ask(prompt: String): Free[F, String] = Free.inject[Interact, F](Ask(prompt))
  }
  object Interacts{
    implicit def interacts[F[_]](implicit I: InjectK[Interact, F]): Interacts[F] = new Interacts[F]
  }


  class DataSource[F[_]](implicit I: InjectK[DataOp, F]){
    def addCat(a: String): Free[F, Unit] = Free.inject[DataOp, F](AddCat(a))
    def getAllCats: Free[F, List[String]] = Free.inject[DataOp, F](GetAllCats())
  }
  object DataSource {
    implicit def dataSource[F[_]](implicit I: InjectK[DataOp, F]): DataSource[F] = new DataSource[F]
  }

  //
  // Definición de un programa.
  //
  def program(implicit I: Interacts[CatsApp], D: DataSource[CatsApp]): Free[CatsApp, Unit] = {

    import I._
    import D._

    for{
      cat <- ask("Name?")
      _ <- addCat(cat)
      cats <- getAllCats
      _ <- tell(cats.toString())
    } yield {}
  }

  //
  // Definición de intérpretes
  //
  object ConsoleCatsInterpreter extends ( Interact ~> Id){
    def apply[A](i: Interact[A]) = i match {
      case Ask(prompt) =>
        println(prompt)
        scala.io.StdIn.readLine()
      case Tell(msg) =>
        println(msg)
    }
  }

  object InMemoryDatasourceInterpreter extends (DataOp ~> Id){

    private[this] val memDataSet = new ListBuffer[String]

    def apply[A](fa: DataOp[A]) = fa match {
      case AddCat(a) =>
        memDataSet.append(a)
        ()
      case GetAllCats() => memDataSet.toList
    }

  }

  val interpreter: CatsApp ~> Id = InMemoryDatasourceInterpreter or ConsoleCatsInterpreter

  //
  // Ejecución
  //
  val result: Unit = program.foldMap(interpreter)


}
