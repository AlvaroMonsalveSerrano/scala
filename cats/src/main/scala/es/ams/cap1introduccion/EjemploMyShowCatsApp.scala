package es.ams.cap1introduccion

object EjemploMyShowCatsApp extends App {

  import MyShow.syntax._

  println( "[Syntax] show(69) = " + show(69) )
  println()
  val gato: Cat = Cat(name="gato", age=18, color="Rosa")
  println( "[Syntax] show(69) = " + 69 )
  println()

  println( "[Syntax] =*=>()= " + gato.=*=>() )
  println()
  println( "[Syntax] show()= " + gato.show() )
  println()


}
