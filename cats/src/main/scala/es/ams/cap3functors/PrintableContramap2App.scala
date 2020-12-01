package es.ams.cap3functors

object PrintableContramap2App extends App{

  import PrintableContramap2.sintaxis._

  val numero3 = 3
  println(s"Conversión de un entero a String=${AA_BB(numero3)} ")
  println()

  val numero4 = 4
  println(s"Conversión de un entero a String=${AA_BB(numero4)} ")
  println()

  println(s"Conversión de un entero a String=${AA_BB(numero3) + AA_BB(numero4) } ")
  println()

  println("-------------------------------------------------")
  val stringToInt = (elem:String) => elem.toInt
  val contramapIntToString = BB_AA(stringToInt)

  val string3 = "3"
  println(s"Conversión de un String a Int=${contramapIntToString.format(string3)  }")
  println()

  val string4 = "4"
  println(s"Conversión de un String a Int=${ contramapIntToString.format(string4) }")
  println()

  println(s"Conversión de un String a Int=${ contramapIntToString.format(string3) +  contramapIntToString.format(string4) }")
  println()

}
