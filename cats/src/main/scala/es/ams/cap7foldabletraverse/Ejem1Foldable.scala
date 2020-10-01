package es.ams.cap7foldabletraverse


/**
  * Ejemplos de catamorfismos sencillos.
  */
object Ejem1Foldable extends App{


  def ejemplo1Foldright(): Unit = {
    println(s"1.- foldRight=${ List(1,2,3).foldRight(List.empty[Int])( (e, acc) => e :: acc) }")
    println(s"2.- foldRight=${ List(1,2,3).foldRight(0)( (e, acc) => e + acc ) }")
    println
  }


  def ejemplo1Foldleft(): Unit = {
    println(s"1.- foldLeft=${ List(1,2,3).foldLeft(List.empty[Int])((acc, e) => e :: acc) }")
    println(s"2.- foldLeft=${ List(1,2,3).foldLeft(0)( (acc, e) => acc + e ) }")
    println
  }


  def ejemploSuma(): Unit = {
    println(s"Suma con foldRight=${List(1, 2, 3, 4).foldRight(0)(_ + _)}")
  }


  def ejemploSumaConNumeric(): Unit = {
    import scala.math.Numeric
    def sumaConNumeric[A](list:List[A])(implicit numeric: Numeric[A]): A  =
      list.foldRight(numeric.zero)(numeric.plus)

    println(s"Suma con Numeric=${sumaConNumeric(List(1, 2, 3, 4))}")
    println
  }


  def ejemploSumaConMonoid(): Unit = {
    import cats.Monoid
    import cats.instances.int._ // for Monoid

    def sumaConMonoid[A](list:List[A])(implicit monoid: Monoid[A]): A =
      list.foldRight(monoid.empty)(monoid.combine)

    println(s"Suma con Momoid=${sumaConMonoid(List(1, 2, 3, 4))}")
    println

  }


  def ejemploFilter(): Unit = {
    val elemFilter1: Int = 3
    println(s"List(1, 2, 3, 4) existe el 3?=${List(1, 2, 3, 4).foldRight(false)( (elem, resul) => resul || elem.equals(elemFilter1))}")

    val elemFilter2: Int = 5
    println(s"List(1, 2, 3, 4) existe el 5?=${List(1, 2, 3, 4).foldRight(false)( (elem, resul) => resul || elem.equals(elemFilter2))}")
    println

    def myfilter[A](list: List[A])(func: A => Boolean): List[A] =
      list.foldRight(List.empty[A]) { (item, accum) => if(func(item)) item :: accum else accum }


    println(s"List(1, 2, 3, 4) filtra los pares.=${ myfilter(List(1, 2, 3, 4))(_%2==0)  }")
    println

  }


  def ejemploMap(): Unit = {
    def myMap[A,B](list: List[A])(f: A => B): List[B] = list.foldRight(List.empty[B])( (elem, result) => f(elem) :: result )

    println(s"List(1, 2, 3) map to String=${List(1, 2, 3).foldRight(List.empty[String])( (elem, resul) =>  s"-${elem.toString}-" :: resul)}")
    println(s"List(1, 2, 3) map to String=${ myMap(List(1, 2, 3))( (elem:Int) => s"*${elem.toString}*" ) }")
    println
  }


  def ejemploFlatmap(): Unit = {
    def flatMap[A, B](list: List[A])(func: A => List[B]): List[B] =
      list.foldRight(List.empty[B]) { (item, accum) => func(item) ::: accum }
    println(s"-->>${flatMap(List(1, 2, 3))(a => List(a, a * 10, a * 100))}")
    println
  }


  ejemplo1Foldright()
  ejemplo1Foldleft()
  ejemploSuma()
  ejemploSumaConNumeric()
  ejemploSumaConMonoid()
  ejemploFilter()
  ejemploMap()
  ejemploFlatmap()

}
