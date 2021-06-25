package es.ams.circe.encodingdecoding

import io.circe.syntax._
import io.circe.parser.decode

class EncodingDecodingEjem1Test extends munit.FunSuite {

  val listInt = List(1, 2, 3)

  test("Basic example encode and decode") {

    // Encoding List[Int] to JSON
    val listIntJson = listInt.asJson
    assertEquals(listIntJson.isArray, true)

    // Decoding JSON to List[Int]
    val listIntDecode = listIntJson.as[List[Int]]
    assertEquals(listIntDecode, Right(listInt))
    println(listIntDecode)

    // Decode con parse. Example JSON to List[Int]
    decode[List[Int]]("[1, 2, 3]") match { // Either[Error, List[Int]]
      case Left(error) => fail("ejem1 string to json")
      case Right(value) => {
        println(s"value=${value}")
        assertEquals(value.size, 3)
      }

    }

  }

}
