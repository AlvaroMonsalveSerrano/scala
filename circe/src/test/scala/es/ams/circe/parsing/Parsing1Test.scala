package es.ams.circe.parsing

//import io.circe._
import io.circe.Json
import io.circe.parser._

class Parsing1Test extends munit.FunSuite {

  test("test, example 1 parse: string to object") {
    val rawJson: String =
      """
        |{
        | "foo": "bar",
        | "baz": 123,
        | "list": [4, 5, 6]
        |}
        |""".stripMargin

    parse(rawJson) match { // Either[ParsingFailure, Json]
      case Left(failure) => fail("ejem1 string to json")
      case Right(value) => {
        println(s"value=${value.noSpaces}")
        assert(value.isObject, true)
      }
    }

    val jsonGetOrElse: Json = parse(rawJson).getOrElse(Json.Null)
    println(s"value=${jsonGetOrElse.noSpaces}")
    assert(jsonGetOrElse.isObject, true)

  }

}
