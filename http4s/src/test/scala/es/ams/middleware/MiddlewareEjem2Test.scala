package es.ams.middleware

import cats.effect._
import org.http4s._
import org.http4s.util.CaseInsensitiveString
import org.http4s.implicits._

class MiddlewareEjem2Test extends munit.FunSuite {

  test("Test service MiddlewareEjem2 OK") {
    val service     = MiddlewareEjem2.service
    val requestTest = Request[IO](Method.GET, uri"/middleware/test")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
  }

  test("Test service MiddlewareEjem1 KO") {
    val service     = MiddlewareEjem2.service
    val requestTest = Request[IO](Method.GET, uri"/middleware")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

  test("Test wrappedService MiddlewareEjem1 OK") {
    val service     = MiddlewareEjem2.wrappedService
    val requestTest = Request[IO](Method.GET, uri"/middleware/test")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
    assertEquals(result.headers.get(CaseInsensitiveString("SomeKey")).get.value, "SomeValue")
  }

  test("Test wrappedService MiddlewareEjem1 KO") {
    val service     = MiddlewareEjem2.wrappedService
    val requestTest = Request[IO](Method.GET, uri"/middleware")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

  test("Test httpRoute MiddlewareEjem1 OK") {
    val service     = MiddlewareEjem2.httpRoute
    val requestTest = Request[IO](Method.GET, uri"/middleware/test")
    val result      = service.run(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
  }

  test("Test httpRoute MiddlewareEjem1 KO") {
    val service     = MiddlewareEjem2.httpRoute
    val requestTest = Request[IO](Method.GET, uri"/middleware")
    val result      = service.run(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

}
