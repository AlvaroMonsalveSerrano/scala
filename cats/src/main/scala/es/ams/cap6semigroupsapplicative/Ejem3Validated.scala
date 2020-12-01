package es.ams.cap6semigroupsapplicative


/**
  * Ejemplos de métodos de Validated.
  *
  * Validated no es monádico con lo cual no está la función flatmap; pero, podemos convertirlo a Either y viceversa
  * utilizando la función toEither y toValidated.
  *
  */
object Ejem3Validated extends App{

//  import cats.data.Validated
//  import cats.instances.all._
  import cats.syntax.all._


  /**
    * Funciones básicas
    */
  def ejemplo1(): Unit = {

    println(s"123.valid.map(_ * 100)=${ 123.valid.map(_ * 100) }")
    println()

    println(s"'?'.invalid.leftMap(_.toString)=${ "?".invalid.leftMap(_.toString) }")
    println()

    println(s" 123.valid[String].bimap( _ + '!', _ * 100 ) = ${ 123.valid[String].bimap( _ + "!", _ * 100 )  }")
    println()

// TODO 2.13
//    println(s" 123.invalid[String].bimap( _ + '!', _ * 100 ) = ${ 123.invalid[String].bimap( _ + "!", _ * 100 ) }")
//    println()

    println(s" 'Badness'.invalid[Int] =${ "Badness".invalid[Int] }")
    println()

    println(s" 'Badness'.invalid[Int].toEither =${ "Badness".invalid[Int].toEither }")
    println()

    println(s" 'Badness'.invalid[Int].toEither.toValidated =${ "Badness".invalid[Int].toEither.toValidated }")
    println()

  }


  /**
    * Operaciones con Either para realizar tareas monádicas
    *
    */
  def ejemplo2(): Unit = {

    println(s"41.valid[String].withEither( _.flatMap( n => Right(n + 1)) )=${ 41.valid[String].withEither( _.flatMap( n => Right(n + 1)) ) }")
    println()

    println(s" ${ "fail".invalid[Int].getOrElse(0) } ")
    println()

    println(s" ${ "fail".invalid[Int].fold(_ + "!!!", _.toString ) } ")
    println()

  }


  ejemplo1()
  ejemplo2()


}
