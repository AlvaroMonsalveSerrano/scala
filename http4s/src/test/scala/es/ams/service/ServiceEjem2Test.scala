package es.ams.service

import cats.effect._
import org.http4s._
import org.http4s.implicits._

class ServiceEjem2Test extends munit.FunSuite {

  test("Test ServiceEjem2.tweetService OK") {
    val service     = ServiceEjem2.tweetService
    val requestTest = Request[IO](Method.GET, uri"/tweets/popular")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
  }

  test("Test ServiceEjem2.tweetService KO") {
    val service     = ServiceEjem2.tweetService
    val requestTest = Request[IO](Method.GET, uri"/tweets")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

  test("Test ServiceEjem2.helloWorldService OK") {
    val service     = ServiceEjem2.helloWorldService
    val requestTest = Request[IO](Method.GET, uri"/hello/test")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.Ok)
  }

  test("Test ServiceEjem2.helloWorldService KO") {
    val service     = ServiceEjem2.helloWorldService
    val requestTest = Request[IO](Method.GET, uri"/hello")
    val result      = service.orNotFound(requestTest).unsafeRunSync()

    assertEquals(result.status, Status.NotFound)
  }

  test("Test ServiceEjem2.service (compose service) OK") {
    val service = ServiceEjem2.services

    val requestTestHello = Request[IO](Method.GET, uri"/hello/test")
    val resultHello      = service.orNotFound(requestTestHello).unsafeRunSync()
    assertEquals(resultHello.status, Status.Ok)

    val requestTestTweet = Request[IO](Method.GET, uri"/tweets/popular")
    val resultTweet      = service.orNotFound(requestTestTweet).unsafeRunSync()
    assertEquals(resultTweet.status, Status.Ok)
  }

  test("Test httpApp OK") {
    val httpApp = ServiceEjem2.httpApp

    val requestTestHello = Request[IO](Method.GET, uri"/hello/test")
    val resultHello      = httpApp.run(requestTestHello).unsafeRunSync()

    assertEquals(resultHello.status, Status.Ok)

    val requestTestTweet = Request[IO](Method.GET, uri"/api/tweets/popular")
    val resultTweet      = httpApp.run(requestTestTweet).unsafeRunSync()
    assertEquals(resultTweet.status, Status.Ok)

  }

  test("Test httpApp KO") {
    val httpApp = ServiceEjem2.httpApp

    val requestTestHello = Request[IO](Method.GET, uri"/hello")
    val resultHello      = httpApp.run(requestTestHello).unsafeRunSync()

    assertEquals(resultHello.status, Status.NotFound)

    val requestTestTweet = Request[IO](Method.GET, uri"/api/tweets")
    val resultTweet      = httpApp.run(requestTestTweet).unsafeRunSync()
    assertEquals(resultTweet.status, Status.NotFound)

  }

}
