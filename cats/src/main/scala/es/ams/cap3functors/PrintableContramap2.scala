package es.ams.cap3functors

trait PrintableContramap2[A,B] { self =>

  def format(elem:A): B

  def contramap( f1:B => A ): PrintableContramap2[B, A] = new PrintableContramap2[B,A] {
    override def format(elem: B): A =  f1(elem)  // Probar a poner: self.format( f1(elem) )
  }

}

object PrintableContramap2 extends PrintableContramap2Instances with PrintableContramap2Sintaxis

trait PrintableContramap2Instances{

  def apply[A,B](implicit P: PrintableContramap2[A,B]): PrintableContramap2[A, B] = P

  implicit val printableIntToString = new PrintableContramap2[Int, String] {
    override def format(elem: Int): String =  elem.toString
  }

  implicit val printableStringToInt = new PrintableContramap2[String, Int] {
    override def format(elem: String): Int = elem.toInt
  }

}

trait PrintableContramap2Sintaxis{
  object sintaxis{
    def AA_BB[A,B](e:A)(implicit P:PrintableContramap2[A,B]) : B = P.format(e)

    def BB_AA[B,A](f1: B => A)(implicit P:PrintableContramap2[A,B]): PrintableContramap2[B, A] = P.contramap( f1 )
  }
}
