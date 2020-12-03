package es.ams.cap2monoidsemigroup

object MyMonoidApp extends App {

  def suma(): Unit = {
    println(" -*- Función lógica Suma -*-")
    import MonoidSuma.syntax._
    println(s"++++(true, true)  =${++++(true, true)}")
    println(s"++++(true, false) =${++++(true, false)}")
    println(s"++++(false, true) =${++++(false, true)}")
    println(s"++++(false, false)=${++++(false, false)}")
    println()
    println(s" emptySuma=${emptySuma} ")
    println()
  }

  def producto(): Unit = {
    println(" -*- Función lógica Producto -*-")
    import MonoidProducto.syntax._
    println(s"****(true, true)  =${****(true, true)}")
    println(s"****(true, false) =${****(true, false)}")
    println(s"****(false, true) =${****(false, true)}")
    println(s"****(false, false)=${****(false, false)}")
    println()
    println(s" emptyProducto=${emptyProducto} ")
    println()
  }

  def fxor(): Unit = {
    println(" -*- Función lógica XOR -*-")
    import MonoidXOR.syntax._
    println(s"xor(true, true)  =${xor(true, true)}")
    println(s"xor(true, false) =${xor(true, false)}")
    println(s"xor(false, true) =${xor(false, true)}")
    println(s"xor(false, false)=${xor(false, false)}")
    println()
    println(s" emptyXOR=${emptyXOR} ")
    println()
  }

  suma()
  producto()
  fxor()

}
