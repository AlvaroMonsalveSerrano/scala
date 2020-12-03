package es.ams.exampleservice

import cats.data.EitherT
import scala.concurrent.{Future}

object TypeEjem1ExampleService {
  type ErrorMsg        = String
  type FunctionString  = (String) => Either[ErrorMsg, String]
  type FunctionEitherT = (String, String) => EitherT[Future, String, Double]
  type FunctionInt     = (Int) => Either[ErrorMsg, Int]

  type Parameter = String
}

import TypeEjem1ExampleService.{FunctionString, FunctionEitherT}

case class OrderInyector private[exampleservice] (
    name: String,
    fOperator: FunctionString,
    fDivideAsync: FunctionEitherT
)
case class OrderOutput private[exampleservice] (name: String)
