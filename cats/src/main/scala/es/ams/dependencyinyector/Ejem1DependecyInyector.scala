package es.ams.dependencyinyector

import cats.syntax.all._

/** Definición de unas funciones para realizar pruebas para no mockear
  */
object Ejem1DependecyInyector {

  type MensajeError  = String
  type GetComponent1 = (String) => Either[MensajeError, String]
  type GetComponent2 = (Int) => Either[MensajeError, Int]

  type ResponseService = Either[MensajeError, String]

  type Parameter = String

  type Service = (GetComponent1, GetComponent2) => (Parameter) => (ResponseService)

  val response1: MensajeError = "Error en Response1"
  val funcGetResponse1: GetComponent1 = (elem: String) => {
    elem.length match {
      case lengthElem: Int if lengthElem > 0 => (elem + " modificado").asRight
      case _                                 => response1.asLeft
    }
  }

  val response2: MensajeError = "Error en Response2"
  val funcGetResponse2: GetComponent2 = (num: Int) => {
    num match {
      case elem: Int if elem > 0 => elem.asRight
      case _                     => response2.asLeft
    }
  }

  val funcService: Service = (getComponent1, getComponent2) =>
    (msg) => {
      for {
        respon1 <- getComponent1(msg)
        respon2 <- getComponent2(msg.length)
      } yield {
        respon1 + "-" + respon2
      }
    }

}

/** Aplicación de prueba.
  */
object Ejem1DependecyInyectorApp extends App {

  import Ejem1DependecyInyector._

  def ejemplo1(): Unit = {
    val message1 = "Mensaje de prueba"
    funcService(funcGetResponse1, funcGetResponse2)(message1) match {
      case Right(msg)  => println(s"Test1=${msg}")
      case Left(error) => println(error)
    }

    val message2 = ""
    funcService(funcGetResponse1, funcGetResponse2)(message2) match {
      case Right(msg)  => println(s"Test2=${msg}")
      case Left(error) => println(error)
    }
  }

  ejemplo1()

}
