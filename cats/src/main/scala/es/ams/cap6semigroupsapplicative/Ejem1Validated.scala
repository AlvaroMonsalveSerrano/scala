package es.ams.cap6semigroupsapplicative

object Ejem1Validated extends App {

  /** Ejemplos de creación básica de Validated.
    */
  def ejemplo1(): Unit = {

    import cats.data.Validated

    println(s"--- EJEMPLO1: Creación de Validated básico  ---")
    //
    // FORMA 1.
    val validated1 = Validated.valid(123)
    println(s"validated_1.1=${validated1}")
    println()

    val validated2 = Validated.invalid(List("Error 1"))
    println(s"validated_1.2=${validated2}")
    println()

    //
    // FORMA 2. Definición de los tipos
    val validated3 = Validated.valid[List[String], Int](123)
    println(s"validated_1.3=${validated3}")
    println()

    val validated4 = Validated.invalid[List[String], Int](List("Error en Validated4"))
    println(s"validated_1.4=${validated4}")
    println()

    //
    // FORMA 3. Creación utilizando la sintaxis.
    import cats.syntax.validated._

    val validated5 = 123.valid[List[String]] // OJO el tipo es el Left
    println(s"validated_1.5=${validated5}")
    println()

    val validated6 = List("Error").invalid[Int] // OJO el tipo es el Left
    println(s"validated_1.6=${validated6}")
    println()

  }

  def ejemplo1_1(): Unit = {

    import cats.data.Validated
    import cats.instances.option._
    import cats.syntax.apply._

    println(s"--- EJEMPLO1_1: Creación de Validated básico y Semigropal ---")
    type Validador[A] = Validated[List[String], A]

    //
    // FORMA 1.
    val validated1: Option[Validador[Int]] = Option(Validated.valid(123))
    println(s"validated_1_1.1=${validated1.get}")
    println()

    val validated2: Option[Validador[Int]] = Option(Validated.invalid(List("Error 1")))
    println(s"validated_1_1.2=${validated2.get}")
    println()

    val reusult1: Option[(Validador[Int], Validador[Int])] = (validated1, validated2).tupled
    println(s"Resultado_1_1.3 Tupla de dos valores =${reusult1}")
    println()

  }

  /** Creación de Validated con applicative.
    *
    * ESTO NO FUNCIONA. no encuentra los valres implícitos de cats.Applicative[ErroresOr]
    */
  def ejemplo2(): Unit = {

//    import cats.data.Validated
//    import cats.syntax.applicative._
//    import cats.syntax.applicativeError._

//    type ErroresOr[A] = Validated[List[String], A]

//    println(s"--- EJEMPLO2: Creación de Validated con applicative  ---")
//    val validated1 = 123.pure[ErroresOr]
//    println(s"validated 2.1=${validated1}")
//    println

//    val validated2 = List("Error 1").raiseError[ErrorsOr,Int]
//    println(s"validated 2.2=${validated2}")
//    println

  }

  /** Ejemplos de Helper de Validated
    */
  def ejemplo3(): Unit = {

    import cats.data.Validated

    val validated1 = Validated.catchOnly[NumberFormatException]("error".toInt)
    println(s"validated 3.1=${validated1}")
    println()

    val validated2 = Validated.catchNonFatal(sys.error("Error"))
    println(s"validated 3.2=${validated2}")
    println()

    val validated3 = Validated.fromTry(scala.util.Try("ErrorNumerico".toInt))
    println(s"validated 3.3=${validated3}")
    println()

    val validated4 = Validated.fromEither[String, Int](scala.util.Left("ErrorEither"))
    println(s"validated 3.4=${validated4}")
    println()

    val validated5 = Validated.fromOption[String, Int](None, "ErrorOption")
    println(s"validated 3.4=${validated5}")
    println()

  }

//  ejemplo1()
//  ejemplo1_1()
//  ejemplo2()
  ejemplo3()

}
