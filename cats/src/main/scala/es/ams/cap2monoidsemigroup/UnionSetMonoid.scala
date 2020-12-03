package es.ams.cap2monoidsemigroup

trait UnionSetMonoid[A] extends Monoid[Set[A]]

object UnionSetMonoid extends UnionSetMonoidInstances with UnionSetMonoidSyntax with UnionSetMonoidLaws

trait UnionSetMonoidInstances {

  def apply[A](implicit USM: UnionSetMonoid[A]): UnionSetMonoid[A] = USM

  implicit val intUnionSetMonoid = new UnionSetMonoid[Int] {

    override def empty: Set[Int] = Set.empty[Int]

    override def combine(x: Set[Int], y: Set[Int]): Set[Int] = x.union(y)
  }
}

trait UnionSetMonoidSyntax {
  object syntax {
    def emptySet[A]()(implicit UM: UnionSetMonoid[A]): Set[A] = UM.empty

    def A__B[A](elem1: Set[A], elem2: Set[A])(implicit UM: UnionSetMonoid[A]): Set[A] = UM.combine(elem1, elem2)

    implicit class UnionSetMonoidOps[A](elem: Set[A])(implicit UM: UnionSetMonoid[A]) {
      def A_union_B(otro: Set[A]): Set[A] = UM.combine(elem, otro)
    }
  }
}

trait UnionSetMonoidLaws {
  import UnionSetMonoid.syntax._

  trait Laws[A] {
    implicit val instances: UnionSetMonoid[A]

    def asociatividad(e1: Set[A], e2: Set[A], e3: Set[A]): Boolean = A__B(A__B(e1, e2), e3) == A__B(e1, A__B(e2, e3))
    def identidadIzquierda(e1: Set[A]): Boolean                    = A__B(e1, emptySet[A]()) == e1
    def identidadDerecha(e1: Set[A]): Boolean                      = A__B(emptySet[A](), e1) == e1
  }

  object Laws {
    def apply[A](implicit UM: UnionSetMonoid[A]): Laws[A] = new Laws[A] {
      implicit val instances = UM
    }
  }
}
