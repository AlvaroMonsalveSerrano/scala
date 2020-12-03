package es.ams.cap8testingasynchronouscodetest

import cats.Id
import es.ams.cap8testingasynchronouscode.Ejem2AsynchronousClient.{UptimeClientFuture, UptimeClientId, UptimeService2}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

import akka.util.Timeout

import cats.implicits._

class Ejem2AsynchronousClientTest extends AnyWordSpec with Matchers {

  val listHostNames          = List("10", "20")
  val listHostNamesIncorrect = List("10", "20", "", "hostError")
  implicit val timeout       = Timeout(2 seconds)

  "Test del libro del cliente asíncrono: prueba síncrona" when {
    "prueba con una lista de host correctos el cliente síncrono" in {
      val cliente = UptimeClientId
      val service = new UptimeService2[Id](cliente)

      val resultId: Id[Int] = service.getTotalUptime(listHostNames)
      resultId shouldBe (30)
    }

    "prueba con una lista de host incorrectos el cliente síncrono" in {
      val cliente = UptimeClientId
      val service = new UptimeService2[Id](cliente)

      val resultId: Id[Int] = service.getTotalUptime(listHostNamesIncorrect)
      println(s"Reusult incorrecto=${resultId}")
      resultId shouldBe (30)
    }
  }

  "Test del libro del cliente asíncrono: prueba Asíncrona" when {
    "prueba con una lista de host correctos el cliente Asíncrono" in {
      val cliente = UptimeClientFuture
      val service = new UptimeService2[Future](cliente)

      val resultFuture: Future[Int] = service.getTotalUptime(listHostNames)
      val result                    = Await.result(resultFuture, timeout.duration)

      result shouldBe (30)
    }

    "prueba con una lista de host incorrectos el cliente Asíncrono" in {
      val cliente = UptimeClientFuture
      val service = new UptimeService2[Future](cliente)

      val resultFuture: Future[Int] = service.getTotalUptime(listHostNamesIncorrect)
      val result                    = Await.result(resultFuture, timeout.duration)

      result shouldBe (30)
    }
  }

}
