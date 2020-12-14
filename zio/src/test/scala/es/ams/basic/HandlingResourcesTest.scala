package es.ams.basic

import es.ams.basic.ExampleBasicOperations.readFile
import zio.{IO, Runtime, UIO}

class HandlingResourcesTest extends BaseClassTest {

  "ZIO handling resources" should "Finalizing" in {

    val finalizer: UIO[Unit]  = UIO.effectTotal(println("Finalizing!"))
    val resultFinalizer: Unit = Runtime.default.unsafeRun(finalizer)
    assertResult(())(resultFinalizer)

    val finalizer2: UIO[Unit] = UIO.effectTotal(println("finally"))
    val operation: UIO[Unit]  = IO.succeed(println("Finalizing 2!")).ensuring(finalizer2)
    val resultOperation       = Runtime.default.unsafeRun(operation)
    assertResult(())(resultOperation)

  }

  it should "Bracket" in {

    val file: UIO[List[String]] = readFile(getURIFileTest(nameFile).getPath)
    val resultFile              = Runtime.default.unsafeRun(file)
    assertResult(List("1 2 3", "4 5 6"))(resultFile)

  }

}
