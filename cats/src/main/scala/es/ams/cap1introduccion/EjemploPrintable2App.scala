package es.ams.cap1introduccion

object EjemploPrintable2App extends App {

  def ejemplo1(): Unit = {
    import Printable2.syntax._
    println("->" + format(69))
    println()
    printer(89)
    println()
    val gato: Cat = Cat(name = "John", age = 18, color = "Blanco")
    println("-->" + format(gato))
    println()

    printer(Cat(name = "John", age = 28, color = "Rojo"))
    println()
  }

  def ejemplo2(): Unit = {
    import Printable2.syntax._

    val gato = Cat(name = "John", age = 38, color = "Verde")
    println(s"Gato:${gato.formatOps()}")
    println()

    val gato2 = Cat(name = "John", age = 48, color = "Rosa")
    gato2.printOps()
    println()

  }

  ejemplo1()
  ejemplo2()

}
