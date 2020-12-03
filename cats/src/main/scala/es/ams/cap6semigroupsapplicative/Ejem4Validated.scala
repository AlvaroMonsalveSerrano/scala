package es.ams.cap6semigroupsapplicative

/** Primera versión.
  */
object Ejem4Validated extends App {

  type Formulario = Map[String, String]

  def getValue(form: Formulario)(nameField: String): Option[String] = {
    assert(!nameField.isEmpty)
    form.get(nameField)
  }

  def nonBlank(msg: String): Boolean = {
    !msg.isEmpty
  }

  def readName(form: Formulario): Either[List[String], String] = {
    val nameForm: Option[String] = getValue(form)("name")
    nameForm match {
      case Some(name) if nonBlank(name)  => Right(name)
      case Some(name) if !nonBlank(name) => Left(List("Name not valid"))
      case None                          => Left(List("Name not valid"))
      case Some(_)                       => Left(List("Name not valid"))
    }
  }

  def parseInt(age: String): Int = {
    assert(!age.isEmpty)
    age.toInt
  }

  def nonNegative(num: Int): Boolean = {
    (num > 0)
  }

  def readAge(form: Formulario): Either[List[String], String] = {
    val ageForm: Option[String] = getValue(form)("age")
    ageForm match {
      case Some(age) if nonNegative(parseInt(age))  => Right(age)
      case Some(age) if !nonNegative(parseInt(age)) => Left(List("Age not valid"))
      case None                                     => Left(List("Age not valid"))
      case Some(_)                                  => Left(List("Age not valid"))
    }
  }

  val formHtml: Formulario = Map("name" -> "Pepito", "age" -> "40")

  val formHtmlKO1: Formulario = Map("name" -> "", "age" -> "40")

  val formHtmlKO2: Formulario = Map("name" -> "Pepito", "age" -> "0")

  def test(): Unit = {

    import cats.data.Validated
    import cats.syntax.all._

    import cats.instances.list._

    println(getValue(formHtml)("name").get)
    println(getValue(formHtml)("age").get)

    println(readName(formHtml))
    println(readAge(formHtml))

    type ValidatedForm[A] = Validated[List[String], A]

    val valid1_1: ValidatedForm[String] = Validated.fromEither[List[String], String](readName(formHtml))
    val valid1_2: ValidatedForm[String] = Validated.fromEither[List[String], String](readAge(formHtml))

    val resultado1 = (valid1_1, valid1_2).tupled
    println(s"resultado1=${resultado1}")
    println()

    val valid2_1: ValidatedForm[String] = Validated.fromEither[List[String], String](readName(formHtmlKO1))
    val valid2_2: ValidatedForm[String] = Validated.fromEither[List[String], String](readAge(formHtmlKO1))

    val resultado2 = (valid2_1, valid2_2).tupled
    println(s"resultado2=${resultado2}")
    println()

    val valid3_1: ValidatedForm[String] = Validated.fromEither[List[String], String](readName(formHtmlKO2))
    val valid3_2: ValidatedForm[String] = Validated.fromEither[List[String], String](readAge(formHtmlKO2))

    val resultado3 = (valid3_1, valid3_2).tupled
    println(s"resultado3=${resultado3}")
    println()

  }

  test()

}
