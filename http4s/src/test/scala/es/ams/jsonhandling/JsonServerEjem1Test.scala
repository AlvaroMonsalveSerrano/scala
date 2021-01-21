package es.ams.jsonhandling

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import munit.CatsEffectSuite
import es.ams.jsonhandling.JsonServerEjem1.jsonService

class JsonServerEjem1Test extends CatsEffectSuite {

  test("json2 return status code 200") {
    assertIO(resultJsonServiceGet(uri"/json2/pepe").map(_.status), Status.Ok)
  }

  test("json2 return message") {
    assertIO(resultJsonServiceGet(uri"/json2/pepe").flatMap(_.as[String]), "{\n  \"hello\" : \"pepe\"\n}")
  }

  test("json3 return status code 200") {
    assertIO(resultJsonServiceGet(uri"/json3/pepe").map(_.status), Status.Ok)
  }

  test("json3 return message") {
    assertIO(resultJsonServiceGet(uri"/json3/pepe").flatMap(_.as[String]), "{\"field\":\"pepe\"}")
  }

  test("json-hello1 return status code 200") {
    assertIO(resultJsonServiceGet(uri"/hello1/pepe/20").map(_.status), Status.Ok)
  }

  test("json-hello1 return message") {
    assertIO(resultJsonServiceGet(uri"/hello1/pepe/20").flatMap(_.as[String]), "{\"field\":\"pepe20\"}")
  }

  private[this] def resultJsonServiceGet(uriValue: Uri): IO[Response[IO]] = {
    val getService = Request[IO](Method.GET, uriValue)
    jsonService.orNotFound(getService)
  }

//  test("json-hello2 return status code 200") {
//    assertIO(resultJsonServicePost(uri"/hello2").map(_.status), Status.Ok)
//  }
//
//  private[this] def resultJsonServicePost(uriValue: Uri): IO[Response[IO]] = {
//    val paramsArg   = Map("field1" -> "value1", "field2" -> "value2")
//    val postService = Request[IO](Method.POST, uriValue)
//    postService.withEntity(paramsArg)
////    postService.withEntity(UrlForm("field1" -> "value1", "field2" -> "value2"))
//    jsonService.orNotFound(postService)
//  }

}
