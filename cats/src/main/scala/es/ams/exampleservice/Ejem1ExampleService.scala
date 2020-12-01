package es.ams.exampleservice

import akka.util.Timeout
import cats.data.Kleisli
import es.ams.exampleservice.TypeEjem1ExampleService.{FunctionString, Parameter}

import scala.concurrent.{Await, Future}
import scala.util.Try
import cats.data.EitherT
import cats.implicits._

//import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Ejem1ExampleService extends App{

  //
  // Definición de funciones.
  //
  val functionOperation1: FunctionString = (param: String) => {
    param match {
      case "ValueOK" => Right("OK")
      case _ => Left("KO: Value not valid.")
    }
  }

  def parseDouble(s: String): Either[String, Double] =
    Try(s.toDouble).map(Right(_)).getOrElse(Left(s"$s is not a number."))

  def divide(a: Double, b: Double): Either[String, Double] =
    Either.cond(b != 0, a / b, "Cannot divide by zero")

  def parseDoubleAsync(s: String): Future[Either[String, Double]] =
    Future.successful(parseDouble(s))

  def divideAsync(a: Double, b: Double): Future[Either[String, Double]] =
    Future.successful(divide(a,b))

  val functionDivisionProgramAsyncEitherT = (inputA: String, inputB: String) => {
    for {
      a <- EitherT(parseDoubleAsync(inputA))
      b <- EitherT(parseDoubleAsync(inputB))
      result <- EitherT(divideAsync(a,b))
    }yield {
      result
    }
  }

  //
  // Definición de un Servicio.
  //
  object Service{


    def businessFunction1(param1: Parameter): Kleisli[List, OrderInyector, OrderOutput] =

      Kleisli[List, OrderInyector, OrderOutput]{ (inyectorFunction: OrderInyector) => {
        val result = for{
            resultFunction <- inyectorFunction.fOperator(param1)
          }yield{ resultFunction }
        result match {
          case Right(valueResult) => List(OrderOutput(valueResult))
          case Left(error) =>  List(OrderOutput(s"ERROR: ${error}"))
        }
      }

    }

    def businessFunction2(inputA: String, inputB: String): Kleisli[List, OrderInyector, OrderOutput] =
      Kleisli[List, OrderInyector, OrderOutput]{ (inyectorFunction: OrderInyector) => {

        implicit val timeout = Timeout(2 seconds)
        val result = for{
          resultFunction <- inyectorFunction.fDivideAsync(inputA, inputB)
        } yield {resultFunction}

        Await.result(result.value, timeout.duration) match {
          case Right(valueResult) => List(OrderOutput(valueResult.toString))
          case Left(error) =>  List(OrderOutput(s"ERROR: ${error}"))
        }
      }
    }
  }

  def example1(): Unit = {

    val startOrder = OrderInyector("inputOrder1", functionOperation1, functionDivisionProgramAsyncEitherT)

    val paramTest1 = "ValueOK"
    val objService1 = Service.businessFunction1(paramTest1)
    val result1: List[OrderOutput] = objService1.run(startOrder)
    println(s"Result1=${result1}")
    println()

    val paramTest2 = "ValueKO"
    val objService2 = Service.businessFunction1(paramTest2)
    val result2: List[OrderOutput] = objService2.run(startOrder)
    println(s"Result2=${result2}")
    println()


    val objService3 = Service.businessFunction2("20", "4")
    val result3: List[OrderOutput] = objService3.run(startOrder)
    println(s"Result3=${result3}")
    println()


  }

  example1()

}
