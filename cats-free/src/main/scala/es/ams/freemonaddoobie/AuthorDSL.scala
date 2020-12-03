package es.ams.freemonaddoobie

import cats.~>
import cats.free.Free
import cats.free.Free.liftF
import cats.data.State
import cats.effect.IO
import doobie.util.transactor.Transactor.Aux

// Definición del dominio.-----------------------------------------
case class Author(id: Long, name: String)

object AuthorDSL {

  // Definición de tipos.--------------------------------------------
  type Operation[A] = Free[OperationDB, A]

  type OperationState[A] = State[StateDatabase, A]

  type OperationDBResponse[A]       = Either[Exception, A]
  type OperationDBResponseOption[A] = Either[Exception, Option[A]]

  // Definición de la gramática -------------------------------------
  sealed trait OperationDB[A]
  case class CreateSchema()         extends OperationDB[OperationDBResponse[Boolean]]
  case class Insert(author: Author) extends OperationDB[OperationDBResponse[Int]]
  case class Select(key: Int)       extends OperationDB[OperationDBResponseOption[String]]
  case class Delete(key: Int)       extends OperationDB[OperationDBResponse[Int]]

  sealed trait StateDatabase
  case object Init    extends StateDatabase
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
  def pureInterpreter(xa: Aux[IO, Unit]): OperationDB ~> OperationState = new (OperationDB ~> OperationState) {

    override def apply[A](fa: OperationDB[A]): OperationState[A] = fa match {

      case CreateSchema() => {
        val resultCreateSchema: Either[Exception, Boolean] = AuthorRepository.createSchemaIntoMySqlB(xa)
        resultCreateSchema
          .fold(
            ex => State[StateDatabase, OperationDBResponse[Boolean]] { state => (Init, resultCreateSchema) },
            value => State[StateDatabase, OperationDBResponse[Boolean]] { state => (Created, resultCreateSchema) }
          )
      }

      case Insert(author) => {
        State[StateDatabase, OperationDBResponse[Int]] { state =>
          (Created, AuthorRepository.insertAuthorIntoMySql(xa, author))
        }
      }

      case Delete(key) => {
        State[StateDatabase, OperationDBResponse[Int]] { state =>
          (Created, AuthorRepository.deleteAuthorById(xa, key))
        }
      }

      case Select(key) => {
        State[StateDatabase, OperationDBResponseOption[String]] { state =>
          (Created, AuthorRepository.selectAuthorById(xa, key))
        }
      }

    } // apply
  }   // pureInterpreter

}
