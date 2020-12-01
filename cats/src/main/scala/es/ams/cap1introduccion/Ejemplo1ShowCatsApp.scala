package es.ams.cap1introduccion

//
// EJEMPLO PARA IMPORTAR TODAS LAS COSAS Y PODER TRABAJAR SIN PROBLEMA.
//
//    import cats._
//    import cats.instances.all._
//    import cats.syntax.all._
//    import cats.implicits._


object Ejemplo1ShowCatsApp extends App {

  import cats._
  import cats.implicits._

  def ejemplo1(): Unit = {
    val showInt: Show[Int] = Show.apply[Int]
    val showString: Show[String] = Show.apply[String]

    println("showInt.show(69)=" + showInt.show(69) )
    println()
    println("showString.show(\"Esto es un String\")=" + showString.show("Esto es un String"))
    println()

    import cats.syntax.show._
    println("[syntax] 69.show=" + 69.show )
    println()
    println("[Syntax] \"69\".show=" + "69".show )
    println()
  }

  ejemplo1()

}
