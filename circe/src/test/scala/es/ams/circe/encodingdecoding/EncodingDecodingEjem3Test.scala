package es.ams.circe.encodingdecoding

import io.circe._
import io.circe.syntax._
import io.circe.parser._

/** https://circe.github.io/circe/codecs/custom-codecs.html
  */
class EncodingDecodingEjem3Test extends munit.FunSuite {

  test("Custom encoder/decoder") {

    class Thing(val foo: String, val bar: Int) {
      def isOk(): Boolean             = true
      override def toString(): String = s"[Thing: foo=${foo}, bar=${bar}]"
    }

    implicit val encodeThing: Encoder[Thing] = new Encoder[Thing] {
      override def apply(a: Thing): Json = Json.obj(
        ("foo", Json.fromString(a.foo)),
        ("bar", Json.fromInt(a.bar))
      )
    }

    implicit val decodeThing: Decoder[Thing] = new Decoder[Thing] {
      override def apply(c: HCursor): Decoder.Result[Thing] =
        for {
          foo <- c.downField("foo").as[String]
          bar <- c.downField("bar").as[Int]
        } yield {
          new Thing(foo, bar)
        }
    }

    val thingTest = new Thing("testString", 1)
    assertEquals(thingTest.asJson.noSpaces, """{"foo":"testString","bar":1}""")

    val thingJsonTest = """{"foo":"testString","bar":1}"""
    decode[Thing](thingJsonTest) match {
      case Left(error) => fail(s"Decoder error: ${error}")
      case Right(value) => {
        println(s"value=${value}")
        assert(value.isOk(), true)
      }
    }
  }

  test("Custom key types") {

    case class Foo(value: String)

    implicit val fooKeyEncoder: KeyEncoder[Foo] = new KeyEncoder[Foo] {
      override def apply(key: Foo): String = key.value
    }

    implicit val fooKeyDecoder: KeyDecoder[Foo] = new KeyDecoder[Foo] {
      override def apply(key: String): Option[Foo] = Some(Foo(key))
    }

    val mapTest = Map[Foo, Int](
      Foo("key1") -> 123,
      Foo("key2") -> 456
    )

    val jsonMapTest: Json = mapTest.asJson
    assertEquals(jsonMapTest.noSpaces, """{"key1":123,"key2":456}""")

    val fooFromJson = jsonMapTest.as[Map[Foo, Int]]
    assertEquals(fooFromJson, Right(mapTest))

  }

}
