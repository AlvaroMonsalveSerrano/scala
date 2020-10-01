package es.ams.cap4monadas

//import cats.Eval

/*

  SCALA       CATS
  =====       ====

  val      -> now     ==> eager, memoized
  def      -> Always  ==> lazy, memoized
  lazy val -> Later   ==> lazy, not memoized

 */
object Ejem1EvalMonad extends App {


  //
  // Ejemplos de tipos de evaluaciones.
  //
  // [START] -------------------------------------------------------
  //
  def evaluacionInmediataEagerYCacheadaMemoized(): Unit = {
    println(s"-*- Ejemplo de evaluación inmediata(Eaget) y cacheada(Memoized)")
    val x = {
      println("Procesando X")
      Math.random
    }
    println(x)
    println(x)
    println
  }


  def evaluacionPerezosaLazyYNoCacheadaMemoized(): Unit = {
    println(s"-*- Ejemplo de evaluación perezosa(Lazy) y No cacheada(Memoized)")
    def x = {
      println("Procesando X")
      Math.random
    }
    println(x)
    println(x)
    println
  }


  def evaluacionPerezosaLazyYCacheadaMemoized(): Unit = {
    println(s"-*- Ejemplo de evaluación perezosa(Lazy) y cacheada(Memoized)")
    lazy val x = {
      println("Procesando X")
      Math.random
    }
    println(x)
    println(x)
    println
  }
  // [END] -------------------------------------------------------
  //


  //
  // Ejemplos utilizando la mónada Eval
  // [START] -----------------------------------------------------

  def ejemploEvalNow(): Unit = {
    import cats.Eval
    println(s"-*- Ejemplo de evaluación Eval.now (Como definir solo val)")
    val x = Eval.now{
      println("Procesando X")
      Math.random
    }
    println(x.value)
    println(x.value)
    println
  }

  def ejemploEvalAlways(): Unit = {
    import cats.Eval
    println(s"-*- Ejemplo de evaluación Eval.always (Como definir solo def)")
    val x = Eval.always{
      println("Procesando X")
      Math.random
    }
    println(x.value)
    println(x.value)
    println
  }

  def ejemploEvalLater(): Unit = {
    import cats.Eval
    println(s"-*- Ejemplo de evaluación Eval.later (Como definir solo lazy, not memoized)")
    val x = Eval.later{
      println("Procesando X")
      Math.random
    }
    println(x.value)
    println(x.value)
    println
  }

  // [END] -------------------------------------------------------
  //

  evaluacionInmediataEagerYCacheadaMemoized()
  evaluacionPerezosaLazyYNoCacheadaMemoized()
  evaluacionPerezosaLazyYCacheadaMemoized()

  ejemploEvalNow()
  ejemploEvalAlways()
  ejemploEvalLater()


}
