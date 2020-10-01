package es.ams.cap4monadas

import cats.Eval


object Ejem2EvalMonad extends App{

  def ejemplo1(): Unit = {
    println("-- EJEMPLO DE MÓNADA EVAL")
    val greeting = Eval
      .always{ println("Paso 1"); "Hello"}
      .map{ str => println("Paso 2"); s"${str} world"  }
    println(greeting.value)
    println

  }

  def ejemplo2(): Unit = {

    println(s"-- EJEMPLO DE EVAL CON for..comprehension")
    val ans = for{
      a <- Eval.now{ println("Calculating A"); 40 }
      b <- Eval.always{ println("Calculating B "); 2 }
    }yield{
      println("Sumando A y B")
      a + b
    }
    println(s"Calculo1=${ans.value}")
    println
    println(s"Calculo2=${ans.value}")
    println

  }


  def ejemplo3(): Unit = {
    // Eval tiene un método que permite cachear una cadena de computación.
    println(s"-- EJEMPLO DE EVAL CON MEMOIZE.")
    val saying = Eval
      .always{ println("Paso 1"); "Un gato" }
      .map{ str => println("Paso 2"); s"${str} siéntate" }
      .memoize
      .map{ str => println("Paso 3"); s"${str} la alfombra" }

    println(s"Calculo1=${saying.value}")
    println
    println(s"Calculo2=${saying.value}")
    println

  }


  def ejemplo4(): Unit = {
    // página 107
    // Eval tiene funciones como map y flatmap que son trampolines. Esto significa que puedes realizar una secuencia de
    // ejecución de funciones; pero, estas secuencias, pueden no ser seguras, pudiendo desbordar la pila de ejecución.


    println(s"-- EJEMPLO DE EVAL con función defer. Es muy óptimo para procesamiento pesado.")

    // Functión falctorial con modificación con map el resultado.
    // Esta función falla porque llega hasta el final de la pila.
    def factorial(n: BigInt): Eval[BigInt] ={
      if(n==1){
        Eval.now(n)
      }else{
        factorial(n-1).map( _ * n )
      }
    }

    // println(  factorial(50000).value  ) // Esta dun

    def factorial2(n: BigInt): Eval[BigInt] ={
      // Esta función no falla porque con defer se evita llenar la pila
      // La función defer es una función trampolin com flatmap p map, con la cual podemos hacer que una función existente
      // hacer una pila segura.
      if(n==1){
        Eval.now(n)
      }else{
        Eval.defer( factorial2(n-1).map( _ * n ) )
      }
    }

//    println(  factorial(50000).value  )

    println(  factorial2(50000).value  )

  }

  ejemplo1
  ejemplo2
  ejemplo3
  ejemplo4
}
