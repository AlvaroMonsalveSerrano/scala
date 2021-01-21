package es.ams.jsonhandling

import cats.effect.IO
import es.ams.jsonhandling.JsonServerEjem2.{healthyRoute, businessRoute} // businessRoute
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.implicits._

class JsonServerEjem2Test extends CatsEffectSuite {

  test("JsonServerEjem2.healthy return status code 200") {
    assertIO(resultJsonService2Healthy(uri"/healthy").map(_.status), Status.Ok)
  }

  test("JsonServerEjem2.healthy return message") {
    assertIO(resultJsonService2Healthy(uri"/healthy").flatMap(_.as[String]), "Ok!")
  }

  private[this] def resultJsonService2Healthy(uriValue: Uri): IO[Response[IO]] = {
    val getService = Request[IO](Method.GET, uriValue)
    healthyRoute[IO]().orNotFound(getService)
  }

  test("JsonServerEjem2.businessroute return status code 200") {
    assertIO(resultJsonService2Business(uri"/business/pepe").map(_.status), Status.Ok)
  }

  test("JsonServerEjem2.businessroute return message") {
    assertIO(
      resultJsonService2Business(uri"/business/pepe").flatMap(_.as[String]),
      "{\"message\":\"Business response. Param: pepe\"}"
    )
  }

  private[this] def resultJsonService2Business(uriValue: Uri): IO[Response[IO]] = {
    val getService        = Request[IO](Method.GET, uriValue)
    val businessOperation = BusinessOperation.run[IO]
    businessRoute(businessOperation).orNotFound(getService)
  }

}
