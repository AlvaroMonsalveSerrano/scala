package es.ams.cap1introduccion

import cats._

trait MyEq[A] {
  def eq(op1: A, op2: A): Boolean
}

object MyEq extends MyEqInstances with MyEqSyntax

trait MyEqInstances {

  def apply[A](implicit ME: MyEq[A]): MyEq[A] = ME

  implicit val eqInt = new MyEq[Int] {
    override def eq(op1: Int, op2: Int): Boolean = {
      import cats.implicits._
      Eq[Int].eqv(op1, op2)
    }
  }

  implicit val eqString = new MyEq[String] {
    import cats.implicits._
    override def eq(op1: String, op2: String): Boolean = Eq[String].eqv(op1, op2)
  }

  implicit val eqCats = new MyEq[Cat] {
    override def eq(op1: Cat, op2: Cat): Boolean = {
      import cats.implicits._
      Eq[String].eqv(op1.name, op2.name) && Eq[Int].eqv(op1.age, op2.age) && Eq[String].eqv(op1.color, op2.color)
    }
  }

}

trait MyEqSyntax {
  object syntax {
    implicit class MyEqOps[A](elem: A)(implicit ME: MyEq[A]) {
      def ===?(op2: A): Boolean = ME.eq(elem, op2)
    }
  }
}
