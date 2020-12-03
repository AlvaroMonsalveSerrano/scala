package es.ams.cap1introduccion

import cats._
import cats.implicits._

/** Definición de un Type Class MyShow utilizando el type class Show de Cats.
  *
  * @tparam A
  */
trait MyShow[A] {
  def show(elem: A): String
}

object MyShow extends MyShowInstances with MyShowSyntax

trait MyShowInstances {

  def apply[A](implicit S: MyShow[A]): MyShow[A] = S

  implicit val myShowInt = new MyShow[Int] {
    def show(elem: Int): String = {
      Show.apply[Int].show(elem)
    }
  }

  implicit val myShowString = new MyShow[String] {
    def show(elem: String): String = {
      elem.show
    }
  }

  implicit val myShowCat = new MyShow[Cat] {
    def show(elem: Cat): String = {
      elem.name.capitalize.show + " tiene " + elem.age.show + " y es de color " + elem.color.show
    }
  }
}

trait MyShowSyntax {
  object syntax {

    def show[A](elem: A)(implicit S: MyShow[A]): String = S.show(elem)

    implicit class MyShowOps[A](elem: A)(implicit S: MyShow[A]) {
      def show(): String = S.show(elem)

      def =*=>(): String = S.show(elem)
    }

  }
}
