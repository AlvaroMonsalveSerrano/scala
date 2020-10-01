package es.ams.cap2monoidsemigroup

import org.scalatest.{FlatSpec, Matchers}

class MyMonoidTest extends FlatSpec with Matchers{

  "Test de las leyes del Monoide lógico Suma" should "cumple las leyes asociativas y de identidad" in {
    import es.ams.cap2monoidsemigroup.MonoidSuma.Laws
    val laws = Laws.apply
    assert( laws.asociatividad( true, false, true) == true)
    assert( laws.izquierdaIdentidad(true) == true )
    assert( laws.derechaIdentidad(true) == true )
  }

  "Test de las leyes del Monoide lógico Producto" should "cumple las leyes asociativas y de identidad" in {
    import es.ams.cap2monoidsemigroup.MonoidProducto.Laws
    val laws = Laws.apply
    assert( laws.asociatividad( true, false, true) == true)
    assert( laws.izquierdaIdentidad(true) == true )
    assert( laws.derechaIdentidad(true) == true )
  }

  "Test de las leyes del Monoide lógico XOR" should "cumple las leyes asociativas y de identidad" in {
    import es.ams.cap2monoidsemigroup.MonoidXOR.Laws
    val laws = Laws.apply
    assert( laws.asociatividad( true, false, true) == true)
    assert( laws.izquierdaIdentidad(true) == true )
    assert( laws.derechaIdentidad(true) == true )
  }


}
