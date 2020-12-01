package es.ams.cap2monoidsemigroup

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.flatspec.AnyFlatSpec

class UnionSetMonoidTest extends AnyFlatSpec with Matchers{


  "Test de las leyes del Monoide UnionSetMonoid" should "cumple las leyes asociativas y de identidad" in {
    val setTest1 = Set(1,2,3)
    val setTest2 = Set(4,5,6)
    import es.ams.cap2monoidsemigroup.UnionSetMonoid.Laws
    import es.ams.cap2monoidsemigroup.UnionSetMonoid.syntax._

    val unionSet = emptySet[Int]()
    val laws = Laws.apply
    assert( laws.asociatividad( unionSet, setTest1, setTest2) == true )
    assert( laws.identidadIzquierda(setTest1) == true )
    assert( laws.identidadDerecha(setTest1) == true )
  }

  "Test de la sintaxis de UnionSetMonoid" should "se ejecuta correctamente" in {
    val setTest1 = Set(1,2,3)
    val setTest2 = Set(4,5,6)
    val resultadoSet = Set(1,2,3,4,5,6)
    import es.ams.cap2monoidsemigroup.UnionSetMonoid.syntax._

    assert( emptySet[Int]() == Set.empty[Int] )
    assert( A__B(setTest1, setTest2).equals(resultadoSet) )
  }


}
