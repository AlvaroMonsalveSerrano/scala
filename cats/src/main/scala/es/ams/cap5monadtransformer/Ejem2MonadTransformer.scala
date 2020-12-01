package es.ams.cap5monadtransformer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.Try
import akka.util.Timeout
import cats.data.EitherT

import cats.implicits._

import scala.concurrent.duration._

/**
  * Ejemplo de la documentación de Cats. Referencia: https://typelevel.org/cats/datatypes/eithert.html
  */
object Ejem2MonadTransformer extends App{

  implicit val timeout = Timeout(2 seconds)

  def parseDouble(s: String): Either[String, Double] =
    Try(s.toDouble).map(Right(_)).getOrElse(Left(s"$s is not a number."))

  def divide(a: Double, b: Double): Either[String, Double] =
    Either.cond(b != 0, a / b, "Cannot divide by zero")

  def parseDoubleAsync(s: String): Future[Either[String, Double]] =
    Future.successful(parseDouble(s))

  def divideAsync(a: Double, b: Double): Future[Either[String, Double]] =
    Future.successful(divide(a,b))



  // Primer ejemplo. Ejemplo básico de realización de una división de forma síncrona.
  def exampleEitherBasic(): Unit = {

    def divisionProgram(inputA: String, inputB: String): Either[String, Double] =
      for{
        a <- parseDouble(inputA)
        b <- parseDouble(inputB)
        result <- divide(a, b)
      }yield{
        result
      }

    println(s"-*- exampleEither1() -*-")
    println(s"Ejemplo1=${divisionProgram("4", "2")}")
    println()

    println(s"Ejemplo2=${divisionProgram("a", "b")}")
    println()

  }

  // Primer ejemplo. Ejemplo básico de realización de una división de forma asíncrona.
  def exampleFutureEither(): Unit = {



    def divisionProgramAsync(inputA: String, inputB: String): Future[Either[String, Double]] =
      parseDoubleAsync(inputA) flatMap{ eitherA =>
        parseDoubleAsync(inputB) flatMap{ eitherB =>
          (eitherA, eitherB) match {
            case(Right(a), Right(b)) => divideAsync(a, b)
            case(Left(err), _) => Future.successful(Left(err))
            case(_, Left(err)) => Future.successful(Left(err))
          }

        }
      }

    println(s"-*- exampleFutureEither() -*-")
    val result1 = Await.result(divisionProgramAsync("4", "2"), timeout.duration)
    println(s"Ejemplo1=${result1}")
    println()

    val result2 = Await.result(divisionProgramAsync("a", "b"), timeout.duration)
    println(s"Ejemplo2=${result2}")
    println()
  }

  def exampleEiitherT(): Unit = {

    def divisionProgramAsyncEitherT(inputA: String, inputB: String): EitherT[Future, String, Double] =
      for {
        a <- EitherT(parseDoubleAsync(inputA))
        b <- EitherT(parseDoubleAsync(inputB))
        result <- EitherT(divideAsync(a,b))
      }yield {
        result
      }

    println(s"-*- exampleFutureEitherT() -*-")
    val result1 = Await.result(divisionProgramAsyncEitherT("4", "2").value, timeout.duration)
    println(s"Ejemplo1=${result1}")
    println()

  }

//  exampleEitherBasic()
//  exampleFutureEither()
  exampleEiitherT()

}
