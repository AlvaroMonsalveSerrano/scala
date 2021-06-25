package es.ams.circe.encodingdecoding

import io.circe._
//import io.circe.generic.semiauto._
import io.circe.syntax._
//import io.circe.generic.JsonCodec
import io.circe.parser._

/** Semi-automatic
  * https://circe.github.io/circe/codecs/semiauto-derivation.html
  */
class EncodingDecodingEjem2Test extends munit.FunSuite {

//
// Do not run
//
//  case class Foo(a: Int, b: String, c: Boolean)
//
//  implicit val fooDecoder: Decoder[Foo] = deriveDecoder
//  implicit val fooEncoder: Encoder[Foo] = deriveEncoder
//
//  test("Example semi-automatic") {
//
//    val foo1 = Foo(a = 1, b = "1", c = true)
//    println(foo1)
//
//    val foo1AsJson = foo1.asJson
//    println(foo1AsJson)
//
//  }
//
//  test("Annotation JsonCodec") {
//
//    @JsonCodec case class Bar(i: Int, s: String)
//
//    implicit val fooEncoder: Encoder[Bar] = deriveEncoder[Bar]
//
//    val barJson: Json = Bar(1, "1").asJson
//    println(barJson)
//
//  }

  test("Encoder and decoders con con helper methods 1.") {

    case class User(id: Long, firstName: String, lastName: String) {
      def isOk() = true
    }
    object User {
      implicit val decoderUser: Decoder[User] =
        Decoder.forProduct3("id", "firstName", "lastName")(User.apply)

      implicit val encodeUser: Encoder[User] =
        Encoder.forProduct3("id", "firstName", "lastName")(u => (u.id, u.firstName, u.lastName))

    }

    val user = User(id = 1, firstName = "a", lastName = "1")
    assertEquals(user.asJson.isObject, true)
    assertEquals(user.asJson.noSpaces, """{"id":1,"firstName":"a","lastName":"1"}""")

    val userJsonString = """{"id":1,"firstName":"a","lastName":"1"}"""
    decode[User](userJsonString) match {
      case Left(error) => fail(s"Encoder and decoder User error: ${error}")
      case Right(value) => {
        println(s"value=${value}")
        assert(value.isOk(), true)
      }
    }

  }

  test("Encoder and decoders con con helper methods 2. ") {

    case class EntityA(at0: Int, at1: String, at2: Boolean) {
      def isOk() = true
    }
    object EntityA {
      implicit val decoderEntityA: Decoder[EntityA] = Decoder.forProduct3("at0", "at1", "at2")(EntityA.apply)
      implicit val encodeEntityA: Encoder[EntityA] =
        Encoder.forProduct3("at0", "at1", "at2")(elem => (elem.at0, elem.at1, elem.at2))
    }

    case class EntityB(id: Int, name: String, ref: EntityA) {
      def isOk() = true
    }
    object EntityB {
      implicit val decoderA: Decoder[EntityB] = Decoder.forProduct3("id", "name", "ref")(EntityB.apply)
      implicit val encodeA: Encoder[EntityB] =
        Encoder.forProduct3("id", "name", "ref")(elem => (elem.id, elem.name, elem.ref))
    }

    val entityA = EntityA(at0 = 1, at1 = "a", at2 = true)
    val entityB = EntityB(id = 0, name = "name1", ref = entityA)

    assertEquals(entityB.asJson.isObject, true)
    assertEquals(entityB.asJson.noSpaces, """{"id":0,"name":"name1","ref":{"at0":1,"at1":"a","at2":true}}""")

    val entityBEjem = """{"id":0,"name":"name1","ref":{"at0":1,"at1":"a","at2":true}}"""
    decode[EntityB](entityBEjem) match {
      case Left(error) => fail(s"Encoder and decoder User error: ${error}")
      case Right(value) => {
        println(s"value=${value}")
        assert(value.isOk(), true)
      }
    }

  }

}
