package es.ams.cap6semigroupsapplicative

object Ejem1Introduccion extends App {

  /** Ejemplo básico de tratamiento de errores en evaluaciones con soluciones monádicas.
    *
    * Las mónadas son secuencias de ejecución, la evaluación de una expresión depende de
    * la ejecución anterior. Al querer validaar un secuencia de sentencias, la mónada no
    * informa de qué sentencia es la errónea. La mónada solo puede retorna la validación
    * de la primera sentencia.
    */
  def ejemplo1(): Unit = {

    import cats.syntax.either._

    def parseInt(str: String): Either[String, Int] = Either
      .catchOnly[NumberFormatException](str.toInt)
      .leftMap(_ => s"No puede leer ${str}")

    val result1: Either[String, Int] = for {
      a <- parseInt("a")
      b <- parseInt("b")
      c <- parseInt("c")
    } yield (a + b + c)

    println(s"--- Ejemplo 1: Control de errores con soluciones monádicas(KO)---")
    println(s"Resultado1=${result1 /*.right*/}")
    println()

    val result2: Either[String, Int] = for {
      a <- parseInt("1")
      b <- parseInt("2")
      c <- parseInt("3")
    } yield (a + b + c)

    println(s"--- Ejemplo 2: Control de errores con soluciones monádicas(OK)---")
    println(s"Resultado2=${result2.getOrElse(0)}")
    println()

  }

  ejemplo1()

}
