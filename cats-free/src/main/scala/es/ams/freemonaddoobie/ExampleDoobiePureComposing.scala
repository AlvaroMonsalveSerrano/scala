package es.ams.freemonaddoobie

import cats.effect.IO
import cats.free.Free

import doobie.util.ExecutionContexts

import es.ams.freemonaddoobie.AuthorComposingDSL._

/** Ejemplo de aplicación de composición de Free Monads.
  *
  * La funcionalidad que se realiza en el programa es muy básico. Es un ejemplo para anallizar el uso de las Free Monads.
  *
  * Ejemplo de variables de entorno: DDBB_HOST=localhost;DDBB_PORT=3306;DDBB_USER=root;DDBB_PWD=root;
  */
object ExampleDoobiePureComposing extends App {
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  def programBusiness()(implicit
      DB: DBOperations[DoobiePureComposingApp],
      L: LogOperations[DoobiePureComposingApp]
  ): Free[DoobiePureComposingApp, Response[Option[String]]] = {

    import DB._
    import L._

    for {
      _          <- debugLog("Creating database...")
      result     <- createSchema()
      numInsert1 <- insert(Author(0, "AuthorTest1"))
      numInsert2 <- insert(Author(0, "AuthorTest2"))
      numDelete1 <- delete(key = 1)
      nameAuthor <- select(key = 2)
      _          <- infoLog("Created Database.")
    } yield {
      nameAuthor
    }

  }

  val result = programBusiness( /*xa*/ ).foldMap(interpreter)

  println(s"Result=${result}")
  println()

}
