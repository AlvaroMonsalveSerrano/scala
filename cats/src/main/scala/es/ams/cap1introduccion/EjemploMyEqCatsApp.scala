package es.ams.cap1introduccion

object EjemploMyEqCatsApp extends App {

  import MyEq.syntax._

  val string1 = "valor1"
  val string2 = "valor2"
  println( s"string1 ===? string2 = ${string1 ===? string2}")
  println
  println( s"string1 ===? string1 = ${string1 ===? string1}")
  println

  val entero1 = 0
  val entero2 = 1
  println( s"entero1 ===? entero2 = ${entero1 ===? entero2}")
  println
  println( s"entero1 ===? entero1 = ${entero1 ===? entero1}")
  println

  val gato1 = Cat("Pepe",18,"Rosa")
  val gato2 = Cat("Juan",18,"Azul")
  println( s"gato1 ===? entero2 = ${gato1 ===? gato2}")
  println
  println( s"gato1 ===? gato1 = ${gato1 ===? gato1}")
  println


}
