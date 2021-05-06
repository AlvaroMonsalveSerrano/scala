package es.ams.basic

import es.ams.basic.ExampleBasicOperations.{
  readFile,
  readFileCatchAll,
  readFileFallback,
  readFileFold,
  readFileFoldM,
  readFileFoldM2,
  readFileOrDefault,
  readFileRetrying,
  sqrt
}
import zio.clock.Clock
import zio.{IO, Runtime, Task, UIO, ZIO}

import java.io.IOException

class HandlingErrorsTest extends BaseClassTest {

  "ZIO Handling Errors" should "Either" in {

    val zeither: UIO[Either[String, Int]] = IO.fail("Boom!").either
    val result: Either[String, Int]       = Runtime.default.unsafeRun(zeither)
    assertResult(Left("Boom!"))(result)

    val ioInputOK: UIO[Double]  = IO.succeed(4.0)
    val resultIOInputOK: Double = Runtime.default.unsafeRun(sqrt(ioInputOK))
    assert(resultIOInputOK > 0)
    assertResult(2.0)(resultIOInputOK)

    val readFileResult: IO[IOException, List[String]] = readFile(getURIFileTest(nameFile).getPath)
    val resultReadFileOK: List[String]                = Runtime.default.unsafeRun(readFileResult)
    assert(resultReadFileOK.isEmpty === false)
    assertResult(List("1 2 3", "4 5 6"))(resultReadFileOK)

  }

  it should "CatchAll" in {

    val readFileOK: Task[List[String]] = readFileCatchAll(getURIFileTest(nameFile).getPath)
    val resultReadFileOK: List[String] = Runtime.default.unsafeRun(readFileOK)
    assert(resultReadFileOK.isEmpty === false)
    assertResult(List("1 2 3", "4 5 6"))(resultReadFileOK)

    val readFileKO: Task[List[String]] = readFileCatchAll("errorFile.data")
    val resultReadFileKO: List[String] = Runtime.default.unsafeRun(readFileKO)
    assert(resultReadFileKO.isEmpty === false)
    assertResult(List("OK"))(resultReadFileKO)

  }

  it should "CatchSome" in {

    val readFileOK: Task[List[String]] = readFileOrDefault(getURIFileTest(nameFile).getPath)
    val resultReadFileOK: List[String] = Runtime.default.unsafeRun(readFileOK)
    assert(resultReadFileOK.isEmpty === false)
    assertResult(List("1 2 3", "4 5 6"))(resultReadFileOK)

    val readFileKO: Task[List[String]] = readFileOrDefault("errorFile.data")
    val resultReadFileKO: List[String] = Runtime.default.unsafeRun(readFileKO)
    assert(resultReadFileKO.isEmpty === false)
    assertResult(List("OK"))(resultReadFileKO)

  }

  it should "Fallback" in {

    val readFileOK: Task[List[String]] = readFileFallback(getURIFileTest(nameFile).getPath)
    val resultReadFileOK: List[String] = Runtime.default.unsafeRun(readFileOK)
    assert(resultReadFileOK.isEmpty === false)
    assertResult(List("1 2 3", "4 5 6"))(resultReadFileOK)

    val readFileKO: Task[List[String]] = readFileFallback("errorFile.data")
    val resultReadFileKO: List[String] = Runtime.default.unsafeRun(readFileKO)
    assert(resultReadFileKO.isEmpty === false)
    assertResult(List("OK"))(resultReadFileKO)

  }

  it should "Fold" in {

    val readFileOK: Task[List[String]] = readFileFold(getURIFileTest(nameFile).getPath)
    val resultReadFileOK: List[String] = Runtime.default.unsafeRun(readFileOK)
    assert(resultReadFileOK.isEmpty === false)
    assertResult(List("1 2 3", "4 5 6"))(resultReadFileOK)

    val readFileKO: Task[List[String]] = readFileFold("errorFile.data")
    val resultReadFileKO: List[String] = Runtime.default.unsafeRun(readFileKO)
    assert(resultReadFileKO.isEmpty === false)
    assertResult(List("OK"))(resultReadFileKO)

  }

  it should "FoldM" in {

    val readFileOK: Task[List[String]] = readFileFoldM(getURIFileTest(nameFile).getPath)
    val resultReadFileOK: List[String] = Runtime.default.unsafeRun(readFileOK)
    assert(resultReadFileOK.isEmpty === false)
    assertResult(List("1 2 3", "4 5 6"))(resultReadFileOK)

    val readFileKO: Task[List[String]] = readFileFoldM("errorFile.data")
    val resultReadFileKO: List[String] = Runtime.default.unsafeRun(readFileKO)
    assert(resultReadFileKO.isEmpty === false)
    assertResult(List("OK"))(resultReadFileKO)

  }

  it should "FoldM2" in {

    val readFileOK: UIO[List[String]]  = readFileFoldM2(getURIFileTest(nameFile).getPath)
    val resultReadFileOK: List[String] = Runtime.default.unsafeRun(readFileOK)
    assert(resultReadFileOK.isEmpty === false)
    assertResult(List("1 2 3", "4 5 6"))(resultReadFileOK)

    val readFileKO: UIO[List[String]]  = readFileFoldM2("errorFile.data")
    val resultReadFileKO: List[String] = Runtime.default.unsafeRun(readFileKO)
    assert(resultReadFileKO.isEmpty === false)
    assertResult(List("OK"))(resultReadFileKO)

  }

  it should "Retrying" in {

    val readFileOK: ZIO[Clock, Throwable, List[String]] = readFileRetrying(getURIFileTest(nameFile).getPath)
    val resultReadFileOK: List[String]                  = Runtime.default.unsafeRun(readFileOK)
    assert(resultReadFileOK.isEmpty === false)
    assertResult(List("1 2 3", "4 5 6"))(resultReadFileOK)

    val readFileKO: ZIO[Clock, Throwable, List[String]] = readFileRetrying("errorFile.data")
    val resultReadFileKO: List[String]                  = Runtime.default.unsafeRun(readFileKO)
    assert(resultReadFileKO.isEmpty === false)
    assertResult(List("OK"))(resultReadFileKO)

  }

}
