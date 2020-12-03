package es.ams.cap1introduccion

// Definición de entidades.
case class Cat(name: String, age: Int, color: String)

// Definición de Type Class
trait Printable2[A] {
  def format(a: => A): String
}

object Printable2 extends PrintableInstances2 with PrintableSyntax2

trait PrintableInstances2 {
  def apply[A](implicit P: Printable[A]): Printable[A] = P

  implicit val printable2String = new Printable2[String] {
    def format(a: => String): String = a
  }

  implicit val printable2Int = new Printable2[Int] {
    def format(a: => Int): String = a.toString
  }

  implicit val printable2Cat = new Printable2[Cat] {
    def format(a: => Cat): String = a.name + " tiene " + a.age + " y es de color " + a.color
  }

}

trait PrintableSyntax2 {
  object syntax {
    def format[A](elem: => A)(implicit P: Printable2[A]): String = P.format(elem)

    def printer[A](elem: => A)(implicit P: Printable2[A]): Unit = println(s"=>${P.format(elem)}")

    implicit class PrintableSyntax2Ops[A](elem: => A)(implicit P: Printable2[A]) {
      def formatOps(): String = P.format(elem)

      def printOps(): Unit = println(s" ===>${P.format(elem)}")
    }
  }
}
