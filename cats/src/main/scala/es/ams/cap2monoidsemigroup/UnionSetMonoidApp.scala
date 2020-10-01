package es.ams.cap2monoidsemigroup

object UnionSetMonoidApp extends App {


  def ejemplo1(): Unit = {
    val setTest1 = Set(1,2,3)
    val setTest2 = Set(4,5,6)

    import UnionSetMonoid.syntax._
    val unionSet = emptySet[Int]

    println( s"union1= ${A__B(unionSet, setTest1)}" )
    println

    println( s"union2= ${A__B(setTest1, setTest2)}" )
    println
  }

  def ejemplo2(): Unit = {
    import cats.Monoid
    import cats.Semigroup
    import cats.instances.string._

    println(s" Monoid.empty[String]='${Monoid[String].empty}' ")
    println

    println(s" Monoid[String].combine('Hola','Mundo!')='${ Monoid[String].combine("Hola ","Mundo!") }' ")
    println

    println(s" Monoid.apply[String].empty='${Monoid.apply[String].empty}' ")
    println

    println(s" Monoid.apply[String].combine('Hola ','Mundo!')='${ Monoid.apply[String].combine("Hola ","Mundo!") }' ")
    println

    println(s" Semigroup[String].combine('Hola ','Mundo!')='${ Semigroup[String].combine("Hola ","Mundo!") }' ")
    println

    println(s" Semigroup.apply[String].combine('Hola ','Mundo!')='${ Semigroup.apply[String].combine("Hola ","Mundo!") }' ")
    println
  }

  def ejemplo3(): Unit = {
//    import cats.Monoid
    import cats.instances.string._
    import cats.instances.int._
    import cats.instances.option._
    import cats.instances.map._
    import cats.instances.tuple._
    import cats.syntax.semigroup._

    println(s"Ejemplo de composición de funciones 1=${ "Palabra1 " |+| "Palabra2 " |+| "Palabra2"}")
    println

    println(s"Ejemplo de composición de funciones 2=${ Option(1) |+| Option(2) |+| Option(3)}")
    println

    println(s"Ejemplo de composición de funciones 3=${ Map( 1 -> "Uno", 2 -> "Dos") |+| Map(3 -> "tres") }")
    println

    val tupla1 = (1,"uno")
    val tupla2 = (2,"dos")
    println(s"Ejemplo de composición de funciones 4=${ tupla1 |+| tupla2 }")
    println

  }

  ejemplo1()
  ejemplo2()
  ejemplo3()


}
