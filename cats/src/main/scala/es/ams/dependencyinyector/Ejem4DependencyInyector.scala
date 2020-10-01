///*
//package es.ams.dependencyinyector
//
//import cats.data.Reader
//import cats.syntax.all._
//import es.ams.dependencyinyector.typesEjem4.{MensajeError, ParameterString, ServiceOperation}
//
//import scala.language.higherKinds
//
//object typesEjem4{
//  type MensajeError = String
//  type GetComponent1 = (String) => Either[MensajeError, String]
//  type GetComponent2 = (Int) => Either[MensajeError, Int]
//
//  type ResponseService = Either[MensajeError, String]
//
//  type ParameterString = String
//  type ParameterInt = Int
//
//  type ServiceOperation[A] = Reader[ServiceContext, A]
//
//  case class ServiceContext( funcComponent1: GetComponent1,
//                             funcComponent2: GetComponent2 )
//}
//
//
//// TODO REhacer el ejercicio con los cambios de cada componente.
//
//// Ejercicio: convertir la función a un método
//object Component1Ejem4{
//  import typesEjem4._
//
//  val response1: MensajeError = "Error en Response1"
//
//  val doSomething: GetComponent1 = (elem: String) => {
//    elem.length match {
//      case lengthElem: Int if lengthElem > 0 => (elem + " modificado").asRight
//      case _ => response1.asLeft
//    }
//  }
//}
//
//// Ejercicio: convertir la función a un método
//object Component2Ejem4{
//  import typesEjem4._
//
//  val response2: MensajeError = "Error en Response2"
//
//  val doSomething: GetComponent2 = (num: Int) => {
//    num match {
//      case elem: Int if elem > 0 => elem.asRight
//      case _ => response2.asLeft
//    }
//  }
//}
//
//// ------------------------------------------------
//trait Service4[ F[_] ]{
//  def doBusiness(msg: typesEjem4.ParameterString): F[ Either[typesEjem4.MensajeError, String] ]
//}
//
//object Service4Impl extends Service4[typesEjem4.ServiceOperation]{
//  override def doBusiness(msg: ParameterString): ServiceOperation[Either[MensajeError, String]] = Reader{ ctx =>
//        for{
//          response1 <- ctx.funcComponent1(msg).right
//          response2 <- ctx.funcComponent2(msg.length).right
//        }yield{
//          response1 + "-" + response2
//        }
//  }
//}
//
//
//object Ejem4DependencyInyector extends App{
//
//  import typesEjem4._
//
//  def ejemplo1(): Unit = {
//
//    val context = ServiceContext(Component1Ejem4.doSomething, Component2Ejem4.doSomething)
//
//    val message1 = "Mensaje de prueba"
//
//    Service4Impl.doBusiness(message1).run(context) match {
//      case Right(msg) => println(s"Test1=${msg}")
//      case Left(error) => println(error)
//    }
//    println
//
//  }
//
//  ejemplo1()
//
//}
//*/
