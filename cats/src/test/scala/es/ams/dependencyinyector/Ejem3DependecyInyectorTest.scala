package es.ams.dependencyinyector

import cats.syntax.all._
import es.ams.dependencyinyector.typesEjem3.{GetComponent1, GetComponent2, ServiceContext}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpec

class Ejem3DependecyInyectorTest extends AnyWordSpec with Matchers {

  "Example Mock" when {

    "Example OK" in {

      val context = ServiceContext(Component1Ejem3.doSomething, Component2Ejem3.doSomething)

      val msg: String = "prueba"
      val result: String = ServiceImpl.doBusiness(msg).run(context) match {
        case Right(msg)  => { println(msg); msg }
        case Left(error) => error
      }
      result shouldBe (msg + " modificado-6")
    }

    "Example OK: mock component1" in {
      val funcGetResponse1Mock: GetComponent1 = (num: String) => "mock".asRight

      val context = ServiceContext(funcGetResponse1Mock, Component2Ejem3.doSomething)

      val msg: String = "prueba"
      val result: String = ServiceImpl.doBusiness(msg).run(context) match {
        case Right(msg)  => { println(msg); msg }
        case Left(error) => error
      }
      assert(result.length > 0)
      assert(result.equals("mock-6"))
    }

    "Example OK: mock component2" in {
      val funcComponent2: GetComponent2 = (num: Int) => 0.asRight

      val context = ServiceContext(Component1Ejem3.doSomething, funcComponent2)

      val msg: String = "prueba"
      val result: String = ServiceImpl.doBusiness(msg).run(context) match {
        case Right(msg)  => { println(msg); msg }
        case Left(error) => error
      }
      assert(result.length > 0)
    }

    "Example OK: mock component1 and mock component2" in {
      val funcGetResponse1Mock: GetComponent1 = (num: String) => "mock".asRight
      val funcGetResponse2Mock: GetComponent2 = (num: Int) => 0.asRight

      val context = ServiceContext(funcGetResponse1Mock, funcGetResponse2Mock)

      val msg: String = "prueba"
      val result: String = ServiceImpl.doBusiness(msg).run(context) match {
        case Right(msg)  => { println(msg); msg }
        case Left(error) => error
      }
      assert(result.length > 0)
      assert(result.equals("mock-0"))
    }

  }

}
