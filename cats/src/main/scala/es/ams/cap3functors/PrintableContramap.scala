package es.ams.cap3functors

// Entity
final case class Box[A](elem:A)

// Type class Printable con contramap
trait PrintableContramap[A] { self =>
  def format(elemento:A): String

  // Tenemos un F[A] => F[B]; existe unaEjem3WriterMonad f/ cotramap (f: B => A) --> F[B]=>F[A]
  def contramap[B](f: B => A): PrintableContramap[B] = new PrintableContramap[B] {
    override def format(elemento: B): String =
      self.format( f(elemento) )
  }
}

object PrintableContramap extends PrintableContramapInstances with PrintableContramapSyntax with PrintableContramapLaws

trait PrintableContramapInstances{
  def apply[A](implicit P:PrintableContramap[A]): PrintableContramap[A] = P

  implicit val intPrintable = new PrintableContramap[Int] { self =>
    override def format(elemento: Int): String = elemento.toString
  }

  implicit val doublePrintable = new PrintableContramap[Double] { self =>
    override def format(elemento: Double): String = elemento.toString
  }

  implicit val stringPrintable = new PrintableContramap[String] { self =>
    override def format(elemento: String): String = elemento.toString
  }

  implicit val booleanPrintable = new PrintableContramap[Boolean] { self =>
    override def format(elemento: Boolean): String = elemento match {
      case true => "sí"
      case false => "no"
    }
  }

  implicit def boxPrintable[A](implicit P:PrintableContramap[A]): PrintableContramap[Box[A]]= new PrintableContramap[Box[A]] { self =>
    override def format(elemento: Box[A]): String = P.format( elemento.elem )
  }
}

trait PrintableContramapSyntax{
  object syntax{
    def A_B[A](e:A)(implicit P:PrintableContramap[A]) : String = P.format(e)

    def B_A[B,A](f: B => A)(implicit P:PrintableContramap[A]): PrintableContramap[B] = P.contramap(f) // OJO CON LOS TIPOS EN UNA ES A Y EN OTRO B
  }
}

trait PrintableContramapLaws
