package es.ams.cap4monadas

/** cats.data.State nos permite pasar un estado adicional como parte de un programa.
  * State representa un estado atómico.
  * Podemos modelas estados mutables de una forma pçuramente funcional.
  */
object Ejem1StateMonad extends App {

  /** Creación de un estado: State[S,A] = S => (S,A) ; S, estado; y A, resultado.
    */
  def ejemplo1(): Unit = {

    println(s"--- EJEMPLO1: Definición de un estado y Obtención del estado ---")

    import cats.data.State

    val a = State[Int, String] { state =>
      (state, s"El resultado es $state")
    }

    // Para "arrancar" la mónada tenemos 3 métodos: run, runS, runA. Cada uno retorna una instancia de Eval el cual
    // es usado para mantener la pila segura.

    // Retorna es estado y el resultado
    val (state, result) = a.run(10).value
    println(s"run() estado y resultado: Estado=${state} y el resultado=${result}")
    println()

    // Retorna solo el estado
    val state2 = a.runS(10).value
    println(s"runS() solo el estado=${state2}")
    println()

    // Retorna solo el resultado.
    val resultado2 = a.runA(10).value
    println(s"runA() solo el resultado=${resultado2}")
    println()

  }

  /** Composición y transformación de estados.
    *
    * De la misma manera que con Writer y Reader, el poder de la mónada State reside en la combinación de instancias.
    */
  def ejemplo2(): Unit = {

    import cats.data.State

    println(s"--- EJEMPLO2: Composición de State ---")
    val step1 = State[Int, String] { num =>
      val ans = num + 1
      (ans, s"Resultado de step1= $ans")
    }

    val step2 = State[Int, String] { num =>
      val ans = num * 2
      (ans, s"Resultado de step2= $ans")
    }

    // State esta hilada de estado a estado. La salida de step1 es la entrada de step2.
    val both = for {
      a <- step1
      b <- step2
    } yield {
      (a, b)
    }

    val (state, result) = both.run(20).value
    println(s"run() Combinación de State: Estado=${state} y el resultado=${result}")
    println()

  }

  /** Funciones de estado:
    *   - get: retorna es estado como el resultado.
    *   - set: modifica el estado y retorna unit como el resultado
    *   - pure: ignora el estado y retorna el resultado.
    *   - inspect: extrae el estado en el resulado mediante una función de transformación.
    *   - modify: modifica el estado usando un función de modificación.
    */
  def ejemplo3(): Unit = {

    import cats.data.State

    println(s"--- EJEMPLO3: ejemplo de funciones de State ---")
    val getDemo = State.get[Int]
    println(s"getDemo=${getDemo.run(10).value}")
    println()

    val setDemo = State.set[Int](30)
    println(s"setDemo=${setDemo.run(10).value}")
    println()

    val pureDemo = State.pure[Int, String]("Result")
    println(s"pureDemo=${pureDemo.run(10).value}")
    println()

    val strs = "!"
    val inspectDemo =
      State.inspect[Int, String]((elem: Int) => s"$elem$strs") // OJO! Transforma el estado en un resultado de salida.
    println(s"inspectDemo=${inspectDemo.run(10).value}")
    println()

    val modifyDemo = State.modify[Int](_ + 1) // OJO! Transforma el estado en un resultado de salida.
    println(s"modifyDemo=${modifyDemo.run(10).value}")
    println()

  }

  /** Ejemplo de for comprehension con las funciones básicas de State.
    */
  def ejemplo4(): Unit = {

    println(s"--- EJEMPLO4: Ejemplo de uso de for comprehension ---")
    import cats.data.State
    import cats.data.State._

    // Definición de un progama(Interprete) de cambio de estado.
    val program: State[Int, (Int, Int, Int)] = for {
      a <- get[Int]
      _ <- set[Int](a + 1)
      b <- get[Int]
      _ <- modify[Int](_ + 1)
      c <- inspect[Int, Int](_ * 1000)
    } yield (a, b, c)

    val (state, result) = program.run(1).value
    println(s"run() For comprehension: Estado=${state} y el resultado=${result}")
    println()

  }

  ejemplo1()
  ejemplo2()
  ejemplo3()
  ejemplo4()

}
