package es.ams.freemonaddoobie

import cats.free.Free
import es.ams.freemonaddoobie.AuthorComposingDSL._

/**
  * Ejemplo de aplicación de composición de Free Monads.
  *
  * La funcionalidad que se realiza en el programa es muy básico. Es un ejemplo para anallizar el uso de las Free Monads.
  *
  */
object ExampleDoobiePureComposing extends App{

  def programBusiness(implicit DB: DBOperations[DoobiePureComposingApp],
                      L: LogOperations[DoobiePureComposingApp]): Free[DoobiePureComposingApp, Either[Exception, Option[String]]] = {

    import DB._
    import L._

    for {
      _ <- debugLog("Creating Database...")
      result <- createSchema()
      numInsert1 <- insert( Author(0, "AuthorTest1"))
      numInsert2 <- insert( Author(0, "AuthorTest2"))
      numDelete1 <- delete(key = 1)
      nameAuthor <- select(key = 2)
      _ <- infoLog("Created Database.")
    } yield {
      nameAuthor
    }

  }

  val result = programBusiness.foldMap(interpreter)
  println(s"Result=${result}")
  println(s"Result=${result.getClass}")
  println

}
