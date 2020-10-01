package es.ams.cap4monadas

object Ejemplo2WriterMonad extends App{

  def ejemplo1(): Unit = {

    import cats.data.Writer
    import cats.syntax.writer._
    import cats.instances.vector._
    import cats.syntax.applicative._

    type Logged[A] = Writer[Vector[String], A]

    println("--- Creación de una mónada con for comprehension ---")
    val writer1 = for {
      a <- 10.pure[Logged]
      _ <- Vector("a","b","c").tell
      b <- 32.writer(Vector("x","y","z"))
    }yield{
      a + b
    }
    val (log, result) = writer1.run
    println(s"Log=${log}")
    println(s"Result=${result}")
    println


    println("--- Transformación de los mensajes con mapWritter ---")
    val writer2 = writer1.mapWritten( _.map( _.toUpperCase )  ) // Transformación de todos los mensajes de la mónada Writer
    val (log2, result2) = writer2.run
    println(s"Log2=${log2}")
    println(s"Result2=${result2}")
    println


    // Transformación de los mensajes y el resultado con f. bimap
    println("--- Transformación de los mensajes y resultado en el mismo instante con bimap ---")
    val writer3 = writer1.bimap(
      log => log.map(_.toUpperCase ),
      res => res * 100
    )
    val (log3, result3) = writer3.run
    println(s"Log3=${log3}")
    println(s"Result3=${result3}")
    println


    // Transformación de los mensajes y el resultado con f. bimap
    println("--- Transformación de los mensajes y resultado en el mismo instante con mapBoth ---")
    val writer4 = writer1.mapBoth{ (log, res) =>
      val log2 = log.map(_ + "!" )
      val res2 = res * 1000
      (log2, res2)
    }
    val (log4, result4) = writer4.run
    println(s"Log4=${log4}")
    println(s"Result4=${result4}")
    println

    println("--- Función reset ---")
    val writer5 = writer1.reset
    val (log5, result5) = writer5.run
    println(s"Log5=${log5}")
    println(s"Result5=${result5}")
    println


    println("--- Función swap, Calbia (m, r) => (r, m) ---")
    val writer6 = writer1.swap
    val (log6, result6) = writer6.run
    println(s"Log6=${log6}")
    println(s"Result6=${result6}")
    println
  }

  ejemplo1

}
