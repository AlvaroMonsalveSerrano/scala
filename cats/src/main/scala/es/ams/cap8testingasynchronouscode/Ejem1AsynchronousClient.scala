package es.ams.cap8testingasynchronouscode

//import scala.concurrent.Future
import akka.util.Timeout

import scala.concurrent.duration._
//import cats.instances.string._
//import cats.syntax.semigroup._

import scala.concurrent.{Await, Future}
//import scala.language.postfixOps


//  import cats.instances.option._ // for Applicative Option
import cats.instances.future._ // for Applicative Future
import cats.instances.list._ // for Traverse
import cats.syntax.traverse._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * La clase Ejem1AsynchronoClient define unos componentes que realizan operaciones asíncronas.
  * Para la realización de los test, al ser asíncrona, no se puede realizar la comparación del
  * resultado del servicio con un valor porque salta una excepción.
  *
  * La solución es utilizar los contructores de tipos.
  *
  */
object Ejem1AsynchronoClient extends App{

  implicit val timeout = Timeout(2 seconds)


  // Cliente asíncrono--------------------------------------------------------------------------------------------------
  trait UptimeClient {
    def getUptime(hostname: String): Future[Int]
  }

  object UptimeClient extends UptimeClient{
    override def getUptime(hostname: String): Future[Int] =
      Future(hostname.toInt)
        .recover{
          case e:Exception => 0
        }
  }

  // -------------------------------------------------------------------------------------------------------------------
  class UptimeService(client: UptimeClient) {
    def getTotalUptime(hostnames: List[String]): Future[Int] =
      hostnames.traverse(elem => client.getUptime(elem)).map(_.sum)
  }
  // -------------------------------------------------------------------------------------------------------------------


  def ejemplo1(): Unit = {

    val client = UptimeClient
    val service = new UptimeService(client)
    val listHostName = List("1","2","3")

    val result1 = Await.result( service.getTotalUptime(listHostName), timeout.duration )
    println(s"Resultado1=${result1}")
  }

  ejemplo1()

}
