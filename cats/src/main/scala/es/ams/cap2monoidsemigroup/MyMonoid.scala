package es.ams.cap2monoidsemigroup

trait Semigroup[A] {
  def combine(x: A, y: A): A
}

trait Monoid[A] extends Semigroup[A] {
  def empty: A
}

// Monoide Suma

trait MonoidSuma[A] extends Monoid[A]

object MonoidSuma extends MonoidSumaInstances with MonoidSumaSyntax with MonoidSumaLaws

trait MonoidSumaInstances {
  def apply[A](implicit monoid: MonoidSuma[A]): MonoidSuma[A] = monoid

  implicit val monoidBooleanSuma = new MonoidSuma[Boolean] {
// 1-FORMA
//    override def combine(x: Boolean, y: Boolean): Boolean = (x, y) match{
//      case (true, true ) => true
//      case (true, false) => true
//      case (false, true) => true
//      case (false, false) => false
//    }

    override def combine(x: Boolean, y: Boolean): Boolean = x || y

    override def empty: Boolean = false
  }
}

trait MonoidSumaSyntax {
  object syntax {
    def ++++[A](a: A, b: A)(implicit monoide: MonoidSuma[A]): A = monoide.combine(a, b)

    def emptySuma[A](implicit monoide: MonoidSuma[A]): A = monoide.empty
  }
}

trait MonoidSumaLaws {
  import MonoidSuma.syntax._

  trait Laws[A] {
    // OJO!!! Importante para que compile
    // Se debe de instalar en el constructor apply.
    implicit val instance: MonoidSuma[A]

    def asociatividad(a1: A, a2: A, a3: A): Boolean = ++++(++++(a1, a2), a3) == ++++(a1, ++++(a2, a3))

    def izquierdaIdentidad(a1: A): Boolean = ++++(a1, emptySuma) == a1

    def derechaIdentidad(a1: A): Boolean = ++++(emptySuma, a1) == a1
  }

  object Laws {
    def apply[A](implicit monoide: MonoidSuma[A]): Laws[A] = new Laws[A] {
      implicit val instance: MonoidSuma[A] = monoide // Dfinición de la referencia del trait.
    }

  }
}

// Monoide Producto
trait MonoidProducto[A] extends Monoid[A]
object MonoidProducto   extends MonoidProductoInstances with MonoidProductoSyntax with MonoidProductoLaws

trait MonoidProductoInstances {
  def apply[A](implicit monoid: MonoidProducto[A]): MonoidProducto[A] = monoid

  implicit val monoidProductoSuma = new MonoidProducto[Boolean] {
// 1-FORMA
//    override def combine(x: Boolean, y: Boolean): Boolean = (x, y) match{
//      case (true, true ) => true
//      case (true, false) => false
//      case (false, true) => false
//      case (false, false) => false
//    }

    override def combine(x: Boolean, y: Boolean): Boolean = x && y

    override def empty: Boolean = true
  }
}

trait MonoidProductoSyntax {
  object syntax {
    def ****[A](a: A, b: A)(implicit monoide: MonoidProducto[A]): A = monoide.combine(a, b)

    def emptyProducto[A](implicit monoide: MonoidProducto[A]): A = monoide.empty
  }
}

trait MonoidProductoLaws {
  import MonoidProducto.syntax._

  trait Laws[A] {
    implicit val instance: MonoidProducto[A]
    def asociatividad(a1: A, a2: A, a3: A): Boolean = ****(****(a1, a2), a3) == ****(a2, ****(a2, a3))
    def izquierdaIdentidad(a1: A): Boolean          = ****(a1, emptyProducto) == a1
    def derechaIdentidad(a1: A): Boolean            = ****(emptyProducto, a1) == a1
  }

  object Laws {
    def apply[A](implicit monoide: MonoidProducto[A]): Laws[A] = new Laws[A] {
      implicit val instance: MonoidProducto[A] = monoide
    }
  }
}

// Monoide XOR
trait MonoidXOR[A] extends Monoid[A]
object MonoidXOR   extends MonoidXORInstances with MonoidXORSyntax with MonoidXORLaws

trait MonoidXORInstances {
  def apply[A](implicit monoid: MonoidXOR[A]): MonoidXOR[A] = monoid

  implicit val monoidXORSuma = new MonoidXOR[Boolean] {
// 1-FORMA
//    override def combine(x: Boolean, y: Boolean): Boolean = (x, y) match{
//      case (true, true ) => false
//      case (true, false) => true
//      case (false, true) => true
//      case (false, false) => false
//    }

    override def combine(x: Boolean, y: Boolean): Boolean = (x && !y) || (!x && y)

    override def empty: Boolean = false
  }
}

trait MonoidXORSyntax {
  object syntax {
    def xor[A](a: A, b: A)(implicit monoide: MonoidXOR[A]): A = monoide.combine(a, b)

    def emptyXOR[A](implicit monoide: MonoidXOR[A]): A = monoide.empty
  }
}

trait MonoidXORLaws {
  import MonoidXOR.syntax._
  trait Laws[A] {
    implicit val instances: MonoidXOR[A]

    def asociatividad(a1: A, a2: A, a3: A): Boolean = xor(xor(a1, a2), a3) == xor(a1, xor(a2, a3))
    def izquierdaIdentidad(a1: A): Boolean          = xor(a1, emptyXOR) == a1
    def derechaIdentidad(a1: A): Boolean            = xor(emptyXOR, a1) == a1
  }

  object Laws {
    def apply[A](implicit monoide: MonoidXOR[A]): Laws[A] = new Laws[A] {
      implicit val instances = monoide
    }
  }
}
