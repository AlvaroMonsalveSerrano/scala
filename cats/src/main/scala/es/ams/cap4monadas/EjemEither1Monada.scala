package es.ams.cap4monadas

object EjemEither1Monada extends App {

  def eitherEjem1Scala(): Unit = {
    val either1: Either[String, Int] = Right(10)
    val either2: Either[String, Int] = Right(32)

    // Con Scala 2.12
    val result212 = for {
      a <- either1
      b <- either2
    } yield {
      a + b
    }
    println(s"Resultado2.12=${result212}")
    println()

    // Con Scala 2.11
    val result211 = for {
      a <- either1 //.right
      b <- either2 //.right
    } yield {
      a + b
    }

    println(s"Resultado2.11=${result211}")
    println()
  }

  def eitherEjem1Cats(): Unit = {
//    import cats.instances.all._
    import cats.syntax.all._

    val either1 = 12.asRight[String] // OJO! String es para el valor izquierdo.
    val either2 = 25.asRight[String]

    val result1 = for {
      a <- either1
      b <- either2
    } yield { a + b }

    println(s"Resultado Cats=${result1}")
    println()
  }

  def eitherMetodos(): Unit = {
    println("### Ejemplos de Either ###")
    import cats.syntax.all._
//    import cats.syntax.either._

    val result1 = Either.catchOnly[NumberFormatException]("ERROR!".toInt)
    println(s"result1=${result1}")
    println()
    val result2 = Either.catchOnly[NumberFormatException]("69".toInt)
    println(s"resul2=${result2}")
    println()

    val result3: Either[Throwable, Nothing] = Either.catchNonFatal(sys.error("SYSTEM ERROR"))
    println(s"result3=${result3}")
    println()

  }

  def eitherConstructores(): Unit = {
    println("### Ejemplos de Constructores de Either ###")
    import cats.syntax.all._
//    import cats.syntax.either._

    val result1: Either[String, Int] = Either.fromOption[String, Int](None, "Error")
    println(s"result1=${result1}")
    println()

    val result2: Either[String, Int] = Either.fromOption[String, Int](Option(90), "Error")
    println(s"result1=${result2}")
    println()

  }

  def transformationEither(): Unit = {
    import cats.syntax.all._
//    import cats.syntax.either._
    val either1 = "Error".asLeft[Int].getOrElse(1)
    println(s"result1=${either1}")
    println(s"result1.getClass=${either1.getClass}")
    println()

    val either2 = "Error".asLeft[Int].orElse(2.asRight[String])
    println(s"result2=${either2}")
    println(s"result2.getClass=${either2.getClass}")
    println()

    // Interesante para validaciones.
    val either3 = 1.asRight[String].ensure("Debe de ser no negativo")(elem => elem > 10)
    println(s"-1 > 10?${either3}")
    println()

    // Recover permite "recuperar" la ejecución cuando se produce un resultado Left.
    val either4 = "error".asLeft[Int].recover { case str: String =>
      -1
    }
    println(s"either4=${either4}")
    println()

    val either5 = "error".asLeft[Int].recoverWith {
      case str: String => { print("-" + str + "-"); Right(-1) }
    }
    println(s"either5=${either5}")
    println()

    // Para complementar a map
    val either6 = "foo".asLeft[Int].leftMap(_.reverse)
    println(s"either6=${either6}")
    println()

    val either7 = "foo".asLeft[Int].bimap(_.reverse, _ * 7)
    println(s"either7=${either7}")
    println()

    val either8 = 5.asRight[String].bimap(_.reverse, _ * 7)
    println(s"either8=${either8}")
    println()

    // Convierte lo de la izquierda en derecha y, lo de la izquierda en derecha.
    val either9 = 123.asRight[String].swap
    println(s"either9=${either9}")
    println()
  }

//  eitherEjem1Scala()
//  eitherEjem1Cats()
  eitherMetodos()
//  eitherConstructores()
//  transformationEither()

}
