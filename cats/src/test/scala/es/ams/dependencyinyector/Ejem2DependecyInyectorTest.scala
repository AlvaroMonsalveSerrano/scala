package es.ams.dependencyinyector

import org.scalatest.{Matchers, WordSpec}

import es.ams.dependencyinyector.typesEjem2.{GetComponent1, GetComponent2}

import cats.syntax.all._

class Ejem2DependecyInyectorTest extends WordSpec with Matchers {


  "Example Mock" should {

    "Example OK" in {
        val msg: String = "prueba"
        val result: String = Service.doBusinessActivity(Component1.doSomething, Component2.doSomething)(msg) match {
          case Right(msg) => { println(msg); msg}
          case Left(error) => error
        }
        result shouldBe(msg + " modificado-6")
    }


    "Example OK: mock component1" in {
      val funcGetResponse1Mock: GetComponent1 = (num: String) => "mock".asRight

      val msg: String = "prueba"
      val result: String = Service.doBusinessActivity(funcGetResponse1Mock, Component2.doSomething)(msg) match {
        case Right(msg) => { println(msg); msg}
        case Left(error) => error
      }
      assert(result.length > 0)
      assert(result.equals("mock-6"))
    }


    "Example OK: mock component2" in {
      val funcComponent2: GetComponent2 =  (num: Int) => 0.asRight

      val msg: String = "prueba"
      val result: String = Service.doBusinessActivity(Component1.doSomething, funcComponent2)(msg) match {
        case Right(msg) => { println(msg); msg}
        case Left(error) => error
      }
      assert(result.length > 0)
    }


    "Example OK: mock component1 and mock component2" in {
      val funcGetResponse1Mock: GetComponent1 = (num: String) => "mock".asRight
      val funcGetResponse2Mock: GetComponent2 = (num: Int) => 0.asRight

      val msg: String = "prueba"
      val result: String =Service.doBusinessActivity(funcGetResponse1Mock, funcGetResponse2Mock)(msg) match {
        case Right(msg) => { println(msg); msg}
        case Left(error) => error
      }
      assert(result.length > 0)
      assert(result.equals("mock-0"))
    }


  }

}
