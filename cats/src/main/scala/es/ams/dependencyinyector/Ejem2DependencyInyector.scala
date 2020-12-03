package es.ams.dependencyinyector

import cats.syntax.either._

object typesEjem2 {
  type MensajeError  = String
  type GetComponent1 = (String) => Either[MensajeError, String]
  type GetComponent2 = (Int) => Either[MensajeError, Int]

  type ResponseService = Either[MensajeError, String]

  type ParameterString = String
  type ParameterInt    = Int

  type BusinessService = (GetComponent1, GetComponent2) => ParameterString => ResponseService

}

object Component1 {
  import typesEjem2._

  val response1: MensajeError = "Error en Response1"

  val doSomething: GetComponent1 = (elem: String) => {
    elem.length match {
      case lengthElem: Int if lengthElem > 0 => (elem + " modificado").asRight
      case _                                 => response1.asLeft
    }
  }
}

object Component2 {
  import typesEjem2._

  val response2: MensajeError = "Error en Response2"
  val doSomething: GetComponent2 = (num: Int) => {
    num match {
      case elem: Int if elem > 0 => elem.asRight
      case _                     => response2.asLeft
    }
  }
}

object Service {
  import typesEjem2._

  val doBusinessActivity: BusinessService = (objComp1, objComp2) =>
    (msg) => {
      for {
        respon1 <- objComp1(msg)
        respon2 <- objComp2(msg.length)

      } yield {
        respon1 + "-" + respon2
      }

    }
}

object Ejem2DependencyInyectorApp extends App {

  def ejemplo1(): Unit = {
    val message1 = "Mensaje de prueba"
    Service.doBusinessActivity(Component1.doSomething, Component2.doSomething)(message1) match {
      case Right(msg)  => println(s"Test1=${msg}")
      case Left(error) => println(error)
    }

    val message2 = ""
    Service.doBusinessActivity(Component1.doSomething, Component2.doSomething)(message2) match {
      case Right(msg)  => println(s"Test2=${msg}")
      case Left(error) => println(error)
    }

  }

  ejemplo1()

}
