package es.ams.cap4monadas

import cats.syntax.all._

object EjemEither2Monada extends App{


  def errorHandling(): Unit = {

    //
    // Definición del ADT y tipo.
    //

    // Product hereda de Any. Product tiene funciones de iteración.
    sealed trait      LoginError                         extends Product with Serializable
    final case class  UserNotFound(username:String)      extends LoginError
    final case class  PasswordIncorrect(username:String) extends LoginError
    case object UnexpectedError                    extends LoginError

    case class User(username:String, password:String)

    type LoginResult = Either[LoginError, User]

    // Manejador de error
    def handleError(error: LoginError): Unit = error match  {
      case UserNotFound( u )    => println(s"Usuario no encontrado: ${u}")
      case PasswordIncorrect(u) => println(s"password incorrecta ${u}")
      case UnexpectedError      => println(s"Error inesperado")
    }

    val result1: LoginResult = User("user1","pwd1").asRight // OJO, definido el tipo previamente.
    val result2: LoginResult = UserNotFound("user2").asLeft // OJO, definido el tipo previamente.

    // OJO, Si result1 es Right entonces fb=println; si result1 es Left entonces fa handleError.
    // NO ES COMO EL CATAMORFISMO.
    result1.fold(handleError, println )

    // OJO, Si result1 es Right entonces fb=println; si result1 es Left entonces fa handleError.
    result2.fold(handleError, println )

  }// Fin errorHandling

  def monadError(): Unit = {

    import cats.MonadError
    import cats.instances.all._

    type ErrorOr[A] = Either[String, A]

    val monadError = MonadError[ErrorOr, String]

    val result1 = monadError.pure(42)
    println(s"result1=${result1}")
    println(s"${result1.getClass}") // Retorna de ErrorOr
    println

    val failure = monadError.raiseError("Error de prueba")
    println(s"failure=${failure}")
    println(s"${failure.getClass}") // Retorna de ErrorOr
    println

    val failure2 = monadError.raiseError("Error 2 de prueba")
    println(s"failure=${failure2}")
    println(s"${failure2.getClass}") // Retorna de ErrorOr
    println

    val resultHandle = monadError.handleError(failure){
      case "Error de prueba" => monadError.pure("Error controlado?")
      case _ => monadError.raiseError("Esto no está controlado")
    }
    println(s"resultHandle=${resultHandle}")
    println(s"${resultHandle.getClass}") // Retorna de ErrorOr
    println


    val resultHandle2 = monadError.handleError(failure2){
      case "Error de prueba" => monadError.pure("Error controlado?")
      case _ => monadError.raiseError("Esto no está controlado")
    }
    println(s"resultHandle=${resultHandle2}")
    println(s"${resultHandle2.getClass}") // Retorna de ErrorOr
    println

    // Con ensure, definimos un predicado que se tiene que cumplir.

    println( s"Control numérico 142=${monadError.ensure( monadError.pure(142) )("Número demasiado algo")(elem => elem > 100)} ")
    println( s"Control numérico 42 =${monadError.ensure( monadError.pure(42) )("Número demasiado algo")(elem => elem > 100)} ")
    println()



  }

//  errorHandling()
  monadError()

}
