package es.ams.cap6semigroupsapplicative

/**
  * Ejemplo de Semigroups
  *
  * Un semigroups abarca la notación de composición de pares de contexto.
  * Los semigrous están definidos en cats.syntax.apply
  *
  * La definición de un Semigroup se realiza de la siguiente forma:
  *
  * trait Semigroupal [F[_]]{
  *   def product[A,B] (fa: F[A], fb:F[B]): F[(A,B)]
  * }
  *
  * fa y fb son contextos diferentes que son combinados para obtener F[(A,B)]
  * Esto nos da más libertad que las mónadas.
  *
  * Semigroupal no está definida en la versión con la que se está trabajando.
  * Sí está Semigroup.
  *
  * Con Semigroupal trabajamos con contextos; con Semigroup, trabajamos con valores.
  * En cats utilizamos para Semigroupal: cats.syntax.apply._
  *
  * NOTA.- Monoid= Semigroup mas función pure. Opera con valores.
  *        Semigroupal .- opera con contextos.
  *
  */
object Ejem1Semigroups extends App{



  def ejemplo1_funcionTupled(): Unit = {

    import cats.instances.option._
    import cats.syntax.apply._

    println(s"--- Ejemplo 1: Semigroupal de N valores ---")
    // Equivalente a Semigroupal.product
    // PODEMOS UTILIZAR LA FUNCIÓN tupled HASTA 22 VALORES.
    val reusult1: Option[(Int,String)] = ( Option(123), Option("abc") ).tupled
    println(s"Resultado1.1. Tupla de dos valores =${reusult1}")
    println()


    val reusult2: Option[(Int,String, Boolean)] = ( Option(123), Option("abc"), Option(true) ).tupled
    println(s"Resultado1.2. Tupla de tres valores =${reusult2}")
    println()

  }


  /**
    * cats.apply provee un método llamado mapN que acepta un functor una función con una aridad
    * (hasta 22 valores) con la cual combina los valores.
    */
  def ejemplo2_funcionMapN(): Unit = {

    import cats.instances.option._
    import cats.syntax.apply._

    //
    // Ejemplo con un Case Class
    //
    case class Cat(name:String, born:Int, color:String)

    // Es como crear un Option[Cat]
    val gato: Option[Cat] = ( Option("Nombre"), Option(4), Option("blanco") ).mapN(Cat.apply)
    println(s"Gato=${gato.get}")
    println()


    val sumaDos: (Int, Int) => Int = (a, b) => a + b
    val sumaTres: (Int, Int, Int) => Int = (a, b, c) => a + b + c

    val resultado1 = (Option(1), Option(3)).mapN(sumaDos)
    println(s"Función de sumaDos=${resultado1.get}")
    println()

    val resultado3 = (Option(1), Option(3), None).mapN(sumaTres)
    println(s"Función de sumaTres=${resultado3.get}")
    println()

    // NOTA-1.- Si los parámetros no coinciden con el tipo, el compilador informa.
    // NOTA-2.- Si los parámetros no coinciden con el número, el compilador informa.

  }


// TODO 2.13
//  /**
//    * Ejemplo de apply que acepta contravarianza e Invarianza.
//    *
//    */
//  def ejemplo3_CotramapN_ImapN(): Unit = {
//
//    import cats.Monoid
////    import cats.instances.monoid._
//    import cats.instances.int._
//    import cats.instances.list._
//    import cats.instances.string._
//    import cats.syntax.apply._
//
//
//    case class Cat(nombre:String, nacimiento:Int, comida: List[String])
//
//
//    val tupla_A_Gato: (String, Int, List[String]) => Cat = Cat.apply _
//
//    val gato_A_Tupla: Cat => (String, Int, List[String]) = cat => (cat.nombre, cat.nacimiento, cat.comida)
//
//    // OJO! El monoid es un Semigroup con la función pure.(applicative)
//    implicit val catMonoid: Monoid[Cat] = (
//      Monoid[String],
//      Monoid[Int],
//      Monoid[List[String]]
//    ).imapN(tupla_A_Gato)(gato_A_Tupla)
//
//
//    //
//    // EJEMPLO DE OPERACIONES DE SEMIGROUP |+|
//    //
//    import cats.syntax.semigroup._
//
//    val gato1 = Cat("Gato1", 4, List("Comida1"))
//    val gato2 = Cat("Gato2", 5, List("Comida2"))
//    val gato3 = Cat("Gato3", 6, List("Comida3"))
//    val gato4 = tupla_A_Gato( "gato4", 7, List("Comida4"))
//
//    println(s"--- EJEMPLO DE CONTRAVARIANZA E INVARIANZA ---")
//    println(s"gato1 + gato2=${gato1 |+| gato2}")
//    println()
//
//    println(s"gato1 + gato2 + gato3=${gato1 |+| gato2 |+| gato3 }")
//    println()
//
//    println(s"gato1 + gato4=${gato1 |+| gato4 }")
//    println()
//
//
//
//  }


  ejemplo1_funcionTupled()
  ejemplo2_funcionMapN()
//  ejemplo3_CotramapN_ImapN()



}
