package es.ams.cap1introduccion

trait Printable[A] {
  def format(a: => A):String
}

object Printable extends PrintableInstances with PrintableSyntax

trait PrintableInstances{
  def apply[A](implicit P:Printable[A]): Printable[A] = P

  implicit val printableString = new Printable[String]{
    def format(a: => String): String = a
  }

  implicit val printableInt = new Printable[Int]{
    def format(a: => Int): String = a.toString
  }
}

trait PrintableSyntax{
  object syntax{
    def format[A](elem: => A)(implicit P:Printable[A]): String = P.format(elem)

    def printer[A](elem: => A)(implicit P:Printable[A]): Unit = println(s"=>${P.format(elem)}")
  }
}
