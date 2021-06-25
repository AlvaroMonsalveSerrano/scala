package es.ams.circe.traversing

import io.circe.Decoder.Result
import io.circe._
import io.circe.parser._

class TraversingTest extends munit.FunSuite {

  val json: String =
    """
      |{
      |  "id": "c730433b-082c-4984-9d66-855c243266f0",
      |  "name": "Foo",
      |  "counts": [1, 2, 3],
      |  "values": {
      |    "bar": true,
      |    "baz": 100.001,
      |    "qux": ["a", "b"]
      |  }
      |}  
      |""".stripMargin

  val doc: Json = parse(json).getOrElse(Json.Null)

  def testInit: HCursor = {
    val doc: Json = parse(json).getOrElse(Json.Null)
    assertEquals(doc.isObject, true)

    doc.hcursor
  }

  test("Extracting data") {

    val cursor: HCursor = testInit

    val baz: Result[Double] = cursor.downField("values").downField("baz").as[Double]
    assertEquals(baz, Right(value = 100.001))

    val baz2: Result[Double] = cursor.downField("values").get[Double]("baz")
    assertEquals(baz2, Right(value = 100.001))

    val secondQux: Result[String] = cursor.downField("values").downField("qux").downArray.as[String]
    assertEquals(secondQux, Right(value = "a"))

  }

  test("Transforming data") {
    val cursor: HCursor = testInit

    val nameCursor: Decoder.Result[String] = cursor.downField("name").as[String]
    assertEquals(nameCursor, Right("Foo"))

    // Crea un cursos con el campo name modificado.
    val reversedNameCursor: ACursor = cursor.downField("name").withFocus(_.mapString(_.reverse))
    println(reversedNameCursor.top)

    val reverseName: Json                         = reversedNameCursor.top.get
    val reverseNameString: Decoder.Result[String] = reverseName.hcursor.downField("name").as[String]
    assertEquals(reverseNameString, Right("ooF"))
    assertEquals(nameCursor, Right("Foo"))

  }

}
