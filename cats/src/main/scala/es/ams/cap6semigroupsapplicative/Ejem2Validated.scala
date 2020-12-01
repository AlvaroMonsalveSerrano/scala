package es.ams.cap6semigroupsapplicative


/**
  * Ejemplos de combinación de elementos Validated en un Semigroupal.
  *
  */
object Ejem2Validated extends App{

  /**
    * Ejemplo de un Semigroupal con dos Validated válidos.
    *
    */
  def ejemplo1(): Unit = {

    import cats.data.Validated
    import cats.syntax.all._

    import cats.instances.string._

    type AllErrorOr[A] = Validated[String, A]
    val operador1: AllErrorOr[Int] = Validated.valid[String, Int]( 2 )
    val operador2: AllErrorOr[Int] = Validated.valid[String, Int]( 5 )

    val resultado1 = ( operador1, operador2 ).tupled
    println(s"resultado1.1=${resultado1}")
    println(s"resultado1.2=${resultado1.isValid}")
    println()

  }


  /**
    * Ejemplo de un Semigroupal con un Validated válido y otro inválido.
    *
    */
  def ejemplo2(): Unit = {

    import cats.data.Validated
    import cats.syntax.all._

    import cats.instances.string._

    type AllErrorOr[A] = Validated[String, A]
    val operador1: AllErrorOr[Int] = Validated.valid[String, Int]( 2 )
    val operador2: AllErrorOr[Int] = Validated.invalid[String, Int]( "Error en la operación 2" )

    val resultado1 = ( operador1, operador2 ).tupled
    println(s"resultado2=${resultado1}")
    println(s"resultado2.1=${resultado1.isValid}")
    println()

  }

  /**
    * Ejemplo de un Semigroupal con dos valores inválidos.
    *
    */
  def ejemplo3(): Unit = {

    import cats.data.Validated
    import cats.syntax.all._

    import cats.instances.string._

    type AllErrorOr[A] = Validated[String, A]
    val operador1: AllErrorOr[Int] = Validated.invalid[String, Int]( "Error en la operación 1" )
    val operador2: AllErrorOr[Int] = Validated.invalid[String, Int]( "Error en la operación 2" )

    val resultado1 = ( operador1, operador2 ).tupled
    println(s"resultado3=${resultado1}")
    println(s"resultado3.1=${resultado1.isValid}")
    println()

  }


  /**
    * Ejemplo tratando con Vector
    */
  def ejemplo4(): Unit = {

    import cats.data.Validated
    import cats.syntax.all._

    import cats.instances.vector._

    type AllErrorOr[A] = Validated[Vector[Int], A]
    val operador1: AllErrorOr[Int] = Validated.invalid[Vector[Int], Int]( Vector(400) )
    val operador2: AllErrorOr[Int] = Validated.invalid[Vector[Int], Int]( Vector(500) )

    val resultado1 = ( operador1, operador2 ).tupled
    println(s"resultado4=${resultado1}")
    println(s"resultado4.1=${resultado1.isValid}")

    def funcion (elem: Vector[Int]):Int = {

      if (elem.length > 1){
        111
      } else{
        0
      }
    }

    println(s"resultado4.2=${resultado1.valueOr( funcion )}")
    println()

  }


  /**
    * Ejemplo con NonEmptyVector. Un  NonEmptyVector es un Vector que se garantiza un elemento.
    */
//  def ejemplo5(): Unit = {
//
//    import cats.data.Validated
//    import cats.data.NonEmptyList
//    import cats.syntax.all._
//    import cats.instances.all._
//
//
//    val operador1: Validated[NonEmptyList[String], Int] = NonEmptyList.of("Error base 1.1").invalid[Int]
//    val operador2: Validated[NonEmptyList[String], Int] = NonEmptyList.of("Error base 2.1").invalid[Int]
//
//    val resultado1 = ( operador1, operador2 ).tupled // FALLA. No existe las instancias de NonEmpyList
//    println(s"resultado5=${resultado1}")
//    println
//
//  }

  ejemplo1()
  ejemplo2()
  ejemplo3()
  ejemplo4()
//  ejemplo5()

}
