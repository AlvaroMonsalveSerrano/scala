package es.ams.middleware

import cats.effect._
import org.http4s._
import org.http4s.util.CaseInsensitiveString
import org.http4s.implicits._

class MiddlewareEjem3Test extends munit.FunSuite {

  test("Test service MiddlewareEjem3 OK") {
    val service     = MiddlewareEjem3.service
    val requestTest = Request[IO](Method.GET, uri"/middleware/test")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
  }

  test("Test service MiddlewareEjem3 KO") {
    val service     = MiddlewareEjem3.service
    val requestTest = Request[IO](Method.GET, uri"/middleware")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

  test("Test wrappedService MiddlewareEjem3 OK") {
    val service     = MiddlewareEjem3.wrappedService
    val requestTest = Request[IO](Method.GET, uri"/middleware/test")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
    assertEquals(result.headers.get(CaseInsensitiveString("SomeKey")).get.value, "SomeValue")
  }

  test("Test wrappedService MiddlewareEjem3 KO") {
    val service     = MiddlewareEjem3.wrappedService
    val requestTest = Request[IO](Method.GET, uri"/middleware")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

  test("Test httpRoute MiddlewareEjem3 OK") {
    val service     = MiddlewareEjem3.httpRoute
    val requestTest = Request[IO](Method.GET, uri"/api/middleware/test")
    val result      = service.run(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
  }

  test("Test httpRoute MiddlewareEjem3 KO") {
    val service     = MiddlewareEjem3.httpRoute
    val requestTest = Request[IO](Method.GET, uri"/middleware")
    val result      = service.run(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

}
