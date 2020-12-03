package es.ams.cap1introduccion

/** Ejercicio 1.3: Scala with Cats.
  */
object EjemploPrintableApp extends App {

  def ejemplo1(): Unit = {
    import Printable.syntax._
    println("->" + format(69))
    printer(89)
  }

  ejemplo1()

}
