package es.ams.cap4monadas

/**
  * Ejemplos básico de la mónada Writer.
  *
  */
object Ejem1WriterMonad extends App {

  /*
    La mónada writer se usa para grabar mensajes, errores o datos adicionales sobre una computación y extraer el
    resultado de log al final de la computación.
   */

  def ejemplo1(): Unit = {

    import cats.data.Writer
    //import cats.instances.vector._
    println(s"== EJEMPLO 1 ==")

    val e1 = Writer(Vector(
      "Era el mejor en el tiempo",
      "Era el peor en el tiempo"), 1859)

    println(s"Result1 Writer= ${e1}")
    println()

  }

  def ejemplo2(): Unit = {

    import cats.data.Writer
    import cats.instances.vector._
    import cats.syntax.applicative._

    println(s"== EJEMPLO 2 ==")

    type Logged[A] = Writer[Vector[String], A]

    val var1 = 123.pure[Logged]
    println(s"var1=${var1}")
    println()

  }


  def ejemplo3(): Unit = {

    import cats.syntax.writer._

    println(s"== EJEMPLO 3 ==")

    val vector1 = Vector("msg1", "msg2", "msg3").tell
    println(s"vector1=${vector1}")
    println()

  }

  def ejemplo4(): Unit = {

    import cats.data.Writer
    import cats.syntax.writer._

    println(s"== EJEMPLO 4 ==")

    val a = Writer(Vector("msg1", "msg2", "msg3"), 123) // Creación de la mónada con los mensajes y el resultado.
    println(s"4.1  a=${a}")

    val aResult: Int = a.value // Obtención del resultado
    println(s"4.2  aResult de a=${aResult}")

    val aLog: Vector[String] = a.written // Obtención de los mensajes.
    println(s"4.3  aLog de a=${aLog}")
    println()

    val b = 123.writer(Vector("msg1", "msg2", "msg3")) // Creación de los mensaje de log
    println(s"4.4  b=${b}")
    println()

    val (log, result) = b.run // Obtención de una tupla con los mensaje de lg y el resultado.
    println(s"4.5  Log=${log}")
    println(s"4.6  Result=${result}")

  }

  ejemplo1()
  ejemplo2()
  ejemplo3()
  ejemplo4()

}
