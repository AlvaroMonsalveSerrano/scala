package es.ams.usecases

import zio.{IO, Task, UIO, ZIO}
import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, assert, suite, testM}

import scala.concurrent.Future
import scala.util.Try
import es.ams.basic.ExampleHandlingResource.readFileBracket

import java.net.URI

object BasicOperationsZioTest extends DefaultRunnableSpec {

  val nameFile: String = "file1test.data"
  lazy val future      = Future.successful("Hi!")

  val zfuture: Task[String] = ZIO.fromFuture { implicit ec =>
    future.map(_ => "Goodbye!")
  }

  def putStrLn(line: String): UIO[Unit] =
    ZIO.effectTotal(println(line))

  def getURIFileTest(nameFile: String): URI = this.getClass.getClassLoader.getResource(nameFile).toURI

  def spec = suite("BasicOperation")(
    testM("Int operation") {
      for {
        intNum <- IO.succeed(21).map(_ * 2)
      } yield assert(intNum)(equalTo(42))
    },
    testM("Zipping") {
      for {
        (str, int) <- ZIO.succeed("a").zip(ZIO.succeed(2))
      } yield {
        assert(str)(equalTo("a"))
        assert(int)(equalTo(2))
      }
    },
    testM("From Scala values") {
      for {
        zoption       <- ZIO.fromOption(Some(2))
        zeither       <- ZIO.fromEither(Right("Success"))
        ztry          <- ZIO.fromTry(Try(40 / 2))
        resultZfuture <- zfuture
        resultPut     <- putStrLn("Test")
      } yield {
        assert(zoption)(equalTo(2))
        assert(zeither)(equalTo("Success"))
        assert(ztry)(equalTo(20))
        assert(resultZfuture)(equalTo("Goodbye!"))
        assert(resultPut)(equalTo(()))
      }
    },
    testM("Handling resources: finalizing") {
      val finalizer2 = UIO.effectTotal(println("finally"))
      for {
        finalizer <- UIO.effectTotal(println("Finalizing!"))
      } yield {
        assert(finalizer)(equalTo(()))
      }
      for {
        operation <- IO.succeed(println("Finalizing 2!")).ensuring(finalizer2)
      } yield {
        assert(operation)(equalTo(()))
      }
    },
    testM("Handling resources: bracket") {

      for {
        file <- readFileBracket(getURIFileTest(nameFile).getPath)
      } yield {
        assert(file)(equalTo(List("1 2 3", "4 5 6")))
      }

    }
  )

}
