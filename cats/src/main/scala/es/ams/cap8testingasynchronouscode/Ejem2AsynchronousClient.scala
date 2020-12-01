package es.ams.cap8testingasynchronouscode


import scala.concurrent.Future
import scala.util.{Failure, Success}
//import cats.instances.future._
//import cats.instances.list._
//import cats.syntax.traverse._
//import cats.syntax.applicative._
//import cats.syntax.all
//
//import cats.syntax.functor._

import cats.implicits._
import cats.Applicative

//import cats.Id

import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent._
import scala.concurrent.duration._
//import scala.language.postfixOps

import akka.util.Timeout


object Ejem2AsynchronousClient extends App {

  type Id[A] = A

  implicit val timeout = Timeout(2 seconds)

  // Interfaz del cliente----------------------------------------------------------------------------------------------
  trait UptimeClient2[F[_]]{
    def getUptime(hostname: String): F[Int]
  }

  // Asíncrono
  object UptimeClientFuture extends UptimeClient2[Future]{
    override def getUptime(hostname: String): Future[Int] =
      Future(hostname.toInt)
        .recover{
          case e:Exception => 0
        }
  }


  // Síncrono
  object UptimeClientId extends  UptimeClient2[Id]{
    override def getUptime(hostname: String): Id[Int] = {
      try{
        hostname.toInt
      }catch{
        case e: Exception => 0
      }
    }
  }


//
//  trait RealUptimeClient extends UptimeClient2[Future]{
//    def getUptime(hostname: String): Future[Int]
//  }

//  trait TestUptimeClient extends  UptimeClient2[Id]{
//    def getUptime(hostname: String): Id[Int]
//  }

//  // Definición de la clase de test.
//  class TestUptimeClient(hosts: Map[String, Int]) extends UptimeClient2[Id]{
//    def getUptime(hostname: String): Id[Int] = hosts.getOrElse(hostname, 0)
//  }


  // -------------------------------------------------------------------------------------------------------------------
  class UptimeService2[F[_]](client: UptimeClient2[F])(implicit a: Applicative[F]) {

    def getTotalUptime(hostnames: List[String]): F[Int] =
      hostnames.traverse( client.getUptime  ).map(_.sum)

  }

  //
  // PRUEBA DE EJECUCIÓN
  //

  val clienteId = UptimeClientId
  val serviceId = new UptimeService2[Id](clienteId)
  val listHostNamesSincrono = List("10", "20")
  println(s"Lista de host=${listHostNamesSincrono}")
  println()

  val resultId: Id[Int] = serviceId.getTotalUptime(listHostNamesSincrono)
  println(s"Resultado Síncrono=${resultId}")
  println()


  val clienteFuture = UptimeClientFuture
  val serviceFuture = new UptimeService2[Future](clienteFuture)
  val listHostNamesASincrono = List("10", "20")
  val resultFuture = serviceFuture.getTotalUptime(listHostNamesASincrono)

  resultFuture onComplete{
    case Success(value) => println(s" Resultado Asíncrono=${value}")
    case Failure(exception) => println(s"Exception=${exception}")
  }

//  val result = Await.result(resultFuture, timeout.duration)
//  println(s"->${result}")

}
