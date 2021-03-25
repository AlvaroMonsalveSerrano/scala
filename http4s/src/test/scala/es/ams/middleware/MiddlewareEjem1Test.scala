package es.ams.middleware

import cats.effect._
import org.http4s._
import org.http4s.util.CaseInsensitiveString
import org.http4s.implicits._

class MiddlewareEjem1Test extends munit.FunSuite {

  test("Test service MiddlewareEjem1 OK") {
    val service     = MiddlewareEjem1.service
    val requestTest = Request[IO](Method.GET, uri"/hello/test")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
  }

  test("Test service MiddlewareEjem1 KO") {
    val service     = MiddlewareEjem1.service
    val requestTest = Request[IO](Method.GET, uri"/hello")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

  test("Test wrappedService MiddlewareEjem1 OK") {
    val service     = MiddlewareEjem1.wrappedService
    val requestTest = Request[IO](Method.GET, uri"/hello/test")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
    assertEquals(result.headers.get(CaseInsensitiveString("SomeKey")).get.value, "SomeValue")
  }

  test("Test wrappedService MiddlewareEjem1 KO") {
    val service     = MiddlewareEjem1.wrappedService
    val requestTest = Request[IO](Method.GET, uri"/hello")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

  test("Test httpRoute MiddlewareEjem1 OK") {
    val service     = MiddlewareEjem1.httpRoute
    val requestTest = Request[IO](Method.GET, uri"/hello/test")
    val result      = service.run(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
  }

  test("Test httpRoute MiddlewareEjem1 KO") {
    val service     = MiddlewareEjem1.httpRoute
    val requestTest = Request[IO](Method.GET, uri"/hello")
    val result      = service.run(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

}
