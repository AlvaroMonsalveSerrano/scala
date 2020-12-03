package es.ams.cap3functors

object PrintableApp extends App {

  import PrintableContramap.syntax._

  val entero = 5
  println(s"format(map)==>'${A_B(entero)}'")
  println()

  val enteroEnString = "5"
  val fStringAEntero = (elem: String) => elem.toInt
  println(s"contramap==>${B_A[String, Int](fStringAEntero).format("5")}")
  println()

  // No realiza el cálculo de la suma matemática porque format siempre retorna String.
  println(
    s"1 contramap ('5' + '8')==>${B_A[String, Int](fStringAEntero).format("5") + B_A[String, Int](fStringAEntero).format("8")}"
  )
  println()

  println(s"2 contramap ('5' + '8') (OJO ESTO NO TIENE SENTIDO)==>${B_A[String, Int](fStringAEntero)
    .format("5")
    .toInt + B_A[String, Int](fStringAEntero).format("8").toInt}")
  println()

  val fBooleanAString = (elem: Boolean) => {
    elem match {
      case true  => "-true-"
      case false => "-false-"
    }
  }

  // BOOLEAN
  println(s"A_B(true)=${A_B(true)} ")
  println(s"A_B(false)=${A_B(false)} ")
  println()

  println(s"contramap ('true')==>${B_A[Boolean, String](fBooleanAString).format(true)}")
  println()

  println(s"contramap ('false')==>${B_A[Boolean, String](fBooleanAString).format(false)}")
  println()

  // ENTIDAD Box
  val boxString = Box(elem = "BOX-A")
  println(s"A_B(boxString)==>${A_B(boxString)}")
  println()
  val boxInt = Box(elem = 69)
  println(s"A_B(boxInt)==>${A_B(boxInt)}")
  println()

  val fElemToBox = (elem: Box[Int]) => elem.elem
  val fBoxToElem = (elem: Int) => Box(elem)

////////////////////////
  println(s"contramap (Box(10))==>${B_A[Box[Int], Int](fElemToBox).format(Box[Int](10))}")
  println()

  println(s"contramap (10)==>${B_A[Int, Box[Int]](fBoxToElem).format(10)}")
  println()

  println(Box(12))
////////////////////////

}
