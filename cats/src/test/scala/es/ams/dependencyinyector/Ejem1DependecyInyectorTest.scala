package es.ams.dependencyinyector

import org.scalatest.{Matchers, WordSpec}

import es.ams.dependencyinyector.Ejem1DependecyInyector._

import cats.syntax.all._

class Ejem1DependecyInyectorTest extends WordSpec with Matchers {


  "Example Mock" should {

    "Example OK" in {
        val msg: String = "prueba"
        val result: String = funcService(funcGetResponse1, funcGetResponse2)(msg) match {
          case Right(msg) => { println(msg); msg}
          case Left(error) => error
        }
        result shouldBe(msg + " modificado-6")
    }


    "Example OK: mock component1" in {
      // Función MOCKEADA
      val funcGetResponse2Mock: GetComponent2 = (num: Int) => 0.asRight

      val msg: String = "prueba"
      val result: String = funcService(funcGetResponse1, funcGetResponse2Mock )(msg) match {
        case Right(msg) => { println(msg); msg}
        case Left(error) => error
      }
      assert(result.length > 0)
    }


    "Example OK: mock component2" in {
      // Función mock
      val funcGetResponse1Mock: GetComponent1 = (num: String) => "mock".asRight

      val msg: String = "prueba"
      val result: String = funcService(funcGetResponse1Mock, funcGetResponse2 )(msg) match {
        case Right(msg) => { println(msg); msg}
        case Left(error) => error
      }
      assert(result.length > 0)
      assert(result.equals("mock-6"))
    }

    "Example OK: mock component1 and mock component2" in {
      // Funciones mock
      val funcGetResponse1Mock: GetComponent1 = (num: String) => "mock".asRight
      val funcGetResponse2Mock: GetComponent2 = (num: Int) => 0.asRight

      val msg: String = "prueba"
      val result: String = funcService(funcGetResponse1Mock, funcGetResponse2Mock )(msg) match {
        case Right(msg) => { println(msg); msg}
        case Left(error) => error
      }
      assert(result.length > 0)
      assert(result.equals("mock-0"))
    }


  }

}
