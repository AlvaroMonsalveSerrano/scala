package es.ams.cap6semigroupsapplicative

import cats.data.Validated
import cats.instances.list._
import cats.syntax.all._

/** Copia de Ejem4Validated pero con mejoras
  */
object Ejem5Validated extends App {

  type Form                = Map[String, String]
  type ControlErrorFast[A] = Either[List[String], A]
  type ValidatedForm[A]    = Validated[List[String], A]

  def getValue(form: Form)(campo: String): ControlErrorFast[String] =
    form.get(campo).toRight(List(s"El valor de $campo no está especificado"))

  def nonBlank(nombre: String)(dato: String): ControlErrorFast[String] =
    Right(dato).ensure(List(s"El campo $nombre no debe de ser vacío."))(_.nonEmpty)

  def parseInt(nombre: String)(age: String): ControlErrorFast[Int] =
    Either.catchOnly[NumberFormatException](age.toInt).leftMap(_ => List(s"El campo $nombre debe de ser numérico"))

  def nonNegative(nombre: String)(dato: Int): ControlErrorFast[Int] =
    Right(dato).ensure(List(s"El campo $nombre no es válido"))(_ >= 0)

  def readName(form: Form): ControlErrorFast[String] =
    // Retorna un ControlErrorRapida[String]
    getValue(form)("name")
      .flatMap(elem => nonBlank("name")(elem))

  def readAge(form: Form): ControlErrorFast[Int] =
    //val ageForm: ControlErrorRapida[String] =
    getValue(form)("age")
      .flatMap(nonBlank("age"))
      .flatMap(parseInt("age"))
      .flatMap(nonNegative("age"))

  val formHtml: Form    = Map("name" -> "Pepito", "age" -> "40")
  val formHtmlKO1: Form = Map("name" -> "", "age" -> "40")
  val formHtmlKO2: Form = Map("name" -> "Pepito", "age" -> "-1")
  val formHtmlKO3: Form = Map("name" -> "", "age" -> "-1")

  println(getValue(formHtml)("name"))
  println(getValue(formHtml)("age"))

  println(readName(formHtml))
  println(readAge(formHtml))

  val valid1_1: ValidatedForm[String] = Validated.fromEither(readName(formHtml))
  val valid1_2: ValidatedForm[Int]    = Validated.fromEither(readAge(formHtml))

  val resultado1 = (valid1_1, valid1_2).tupled
  println(s"resultado1=${resultado1}")
  println()

  val valid2_1: ValidatedForm[String] = Validated.fromEither(readName(formHtmlKO1))
  val valid2_2: ValidatedForm[Int]    = Validated.fromEither(readAge(formHtmlKO1))

  val resultado2 = (valid2_1, valid2_2).tupled
  println(s"resultado2=${resultado2}")
  println()

  val valid3_1: ValidatedForm[String] = Validated.fromEither(readName(formHtmlKO2))
  val valid3_2: ValidatedForm[Int]    = Validated.fromEither(readAge(formHtmlKO2))

  val resultado3 = (valid3_1, valid3_2).tupled
  println(s"resultado3=${resultado3}")
  println()

  val valid4_1: ValidatedForm[String] = Validated.fromEither(readName(formHtmlKO3))
  val valid4_2: ValidatedForm[Int]    = Validated.fromEither(readAge(formHtmlKO3))

  val resultado4 = (valid4_1, valid4_2).tupled
  println(s"resultado4=${resultado4}")
  println()

  println(s"1 ->${parseInt("campo")("34")}")
  println()
  println(s"2 ->${parseInt("campo")("")}")
  println()

}
