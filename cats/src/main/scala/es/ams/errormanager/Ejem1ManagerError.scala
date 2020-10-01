package es.ams.errormanager

import scala.util.{Failure, Success, Try}
//import cats.syntax.all._


/**
  * Primera aproximación de tratamiento de errorres con Try.
  */
object Ejem1ManagerError extends App{

  case class EntityEj1(field1: String, field2: Int, field3: String)

  type TypeFunction1 = (String) => Try[String]
  type TypeFunction2 = (String) => Try[Int]
  type TypeFunction3 = (String) => Try[String]

  val businessFunc1: TypeFunction1 = (elem:String) => elem match {
    case value: String if value.length > 0 => Success(value)
    case _ => Failure(new IllegalArgumentException("Error parameter function1"))
  }


  val businessFunc2: TypeFunction2 = (elem:String) => elem match {
    case value: String if value.length > 0 => Success(value.length)
    case _ => Failure(new IllegalArgumentException("Error parameter function2"))
  }


  val businessFunc3: TypeFunction3 = (elem:String) => elem match {
    case value: String if value.length > 0 => Success(value + " :updated")
    case _ => Failure(new IllegalArgumentException("Error parameter function3"))
  }


  /**
    * OJO, EL Try es para ejecución síncrona
    * @param name
    * @return
    */
  def doBusinessService(name:String): Try[EntityEj1] = {
      (for{
          elem1 <- businessFunc1(name)
          elem2 <- businessFunc2(name)
          elem3 <- businessFunc3(name)

        }yield (EntityEj1(elem1, elem2, elem3))
      ).recoverWith{
            case errorFor: Exception => {
                println(s"Error...$errorFor")
                Failure(errorFor)
            }
        }
  }


  // def fold[U](fa: Throwable => U, fb: T => U): U
  val result1 = doBusinessService("Nombre") match {
    case Success(value) => value
    case Failure(error) => error
  }
  println(s"Resultado1=${result1}")
  println


  val result2 = doBusinessService("") match {
    case Success(value) => value
    case Failure(error) => error
  }
  println(s"Resultado2=${result2}")
  println



}
