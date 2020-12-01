package es.ams.cap4monadas



object Ejem2StateMonad extends App{

  def exerciseStateMonad(): Unit = {

    import cats.data.State
    import cats.syntax.applicative._

    type CalcState[A] = State[List[Int], A]

    def operando(num:Int): CalcState[Int] = State[List[Int],Int]{ state =>
      (num::state, num)
    }

    def operator(func: (Int,Int) => Int): CalcState[Int] = State[List[Int], Int]{
      case a :: b :: tail =>
        val ans = func(a,b)
        (ans :: tail, ans)
      case _ => sys.error("Error en el cálculo de la operación")
    }

    def evalOne(sym:String): CalcState[Int] = sym match{
      case sym:String if sym.equals("+") => operator(_ + _)
      case sym:String if sym.equals("-") => operator(_ - _)
      case sym:String if sym.equals("*") => operator(_ * _)
      case sym:String if sym.equals("/") => operator(_ / _)
      case num:String => operando(num.toInt)
    }


    println(s"--- Ejemplo ejecutando secuencialmente ---")
    val (state1, result1) = evalOne("1").run(List.empty[Int]).value
    println(s"Operacion: Estado=${state1} y el resultado=${result1}")
    println()
    val (state2, result2) = evalOne("2").run(state1).value
    println(s"Operacion: Estado=${state2} y el resultado=${result2}")
    println()
    val (state3, result3) = evalOne( "+").run(state2).value
    println(s"Operacion: Estado=${state3} y el resultado=${result3}")
    println()


    println(s"--- Ejemplo ejecutando con FOR..COMPREHENSION ---")
    val ejemplo2 = for{
      _ <- evalOne("1")
      _ <- evalOne("3")
      result <- evalOne("+")

    }yield {result}

    val (stateEjemplo2, resultEjemplo2) =ejemplo2.run(List.empty[Int]).value
    println(s"Con For Comprehension: Estado=${stateEjemplo2} y el resultado=${resultEjemplo2}")
    println()

    // ------------------------------------------
    def evalAll1(input: List[String]): CalcState[Int] = input match {
      case Nil => 0.pure[CalcState]
      case head :: tail => evalOne(head).flatMap( elem => evalAll1(tail))
    }

    val (stateEjemplo3, resultEjemplo3) = evalAll1(List("5","2","-")).run(List.empty[Int]).value
    println(s"evalAll1: Estado=${stateEjemplo3} y el resultado=${resultEjemplo3}")
    println()

    def evalAll2(input: List[String]): CalcState[Int] =  {
      input.foldLeft(0.pure[CalcState]){
        (a,b)=> a.flatMap(_ => evalOne(b))
      }
    }

    val (stateEjemplo4, resultEjemplo4) = evalAll2(List("5","2","-")).run(List.empty[Int]).value
    println(s"evalAll2: Estado=${stateEjemplo4} y el resultado=${resultEjemplo4}")
    println()


    // ---------------------------------------------

  }

  exerciseStateMonad()

}
