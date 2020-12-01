package es.ams.dependencyinyector

import cats.data.Reader
import cats.syntax.either._

//import scala.language.higherKinds

object typesEjem3{
  type MensajeError = String
  type GetComponent1 = (String) => Either[MensajeError, String]
  type GetComponent2 = (Int) => Either[MensajeError, Int]

  type ResponseService = Either[MensajeError, String]

  type ParameterString = String
  type ParameterInt = Int

  type ServiceOperation[A] = Reader[ServiceContext, A]

  case class ServiceContext( funcComponent1: GetComponent1,
                             funcComponent2: GetComponent2 )
}


object Component1Ejem3{
  import typesEjem3._

  val response1: MensajeError = "Error en Response1"

  val doSomething: GetComponent1 = (elem: String) => {
    elem.length match {
      case lengthElem: Int if lengthElem > 0 => (elem + " modificado").asRight
      case _ => response1.asLeft
    }
  }
}

object Component2Ejem3{
  import typesEjem3._

  val response2: MensajeError = "Error en Response2"

  val doSomething: GetComponent2 = (num: Int) => {
    num match {
      case elem: Int if elem > 0 => elem.asRight
      case _ => response2.asLeft
    }
  }
}

// ------------------------------------------------
trait Service3[F[_]]{
  def doBusiness(msg: typesEjem3.ParameterString): F[Either[typesEjem3.MensajeError, String] ]
}


object ServiceImpl extends Service3[typesEjem3.ServiceOperation]{
  override def doBusiness(msg: typesEjem3.ParameterString): typesEjem3.ServiceOperation[Either[typesEjem3.MensajeError, String]] = Reader{ ctx =>
        for{
          response1 <- ctx.funcComponent1(msg)
          response2 <- ctx.funcComponent2(msg.length)
        }yield{
          response1 + "-" + response2
        }
  }
}

object Ejem3DependencyInyector extends App{
  import typesEjem3._

  def ejemplo1(): Unit = {
    val context = ServiceContext(Component1Ejem3.doSomething, Component2Ejem3.doSomething)
    val message1 = "Mensaje de prueba"
    ServiceImpl.doBusiness(message1).run(context) match {
      case Right(msg) => println(s"Test1=${msg}")
      case Left(error) => println(error)
    }
    println()
  }

  ejemplo1()
}
