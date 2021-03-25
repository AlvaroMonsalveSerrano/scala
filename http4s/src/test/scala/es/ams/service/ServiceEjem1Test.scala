package es.ams.service

import cats.effect._
import org.http4s._
import org.http4s.implicits._

class ServiceEjem1Test extends munit.FunSuite {

  test("Test ServiceEjem1 OK") {
    val service     = ServiceEjem1.helloWorldService
    val requestTest = Request[IO](Method.GET, uri"/hello/test")
    val result      = service.run(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
  }

  test("Test ServiceEjem1 KO") {
    val service     = ServiceEjem1.helloWorldService
    val requestTest = Request[IO](Method.GET, uri"/hello")
    val result      = service.run(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

}
