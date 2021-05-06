package es.ams.basic

import es.ams.basic.ExampleHandlingResource.readFileBracket
import zio.{IO, Runtime, Task, UIO}

class HandlingResourcesTest extends BaseClassTest {

  "ZIO handling resources" should "Finalizing" in {

    val finalizer: UIO[Unit]  = UIO.effectTotal(println("Finalizing!"))
    val resultFinalizer: Unit = Runtime.default.unsafeRun(finalizer)
    assertResult(())(resultFinalizer)

    val finalizer2: UIO[Unit] = UIO.effectTotal(println("finally"))
    // ensuring equivale a esquema try/finally. En ensuring se realiza el efecto de la parte de finally.
    val operation: UIO[Unit] = IO.succeed(println("Finalizing 2!")).ensuring(finalizer2)
    val resultOperation      = Runtime.default.unsafeRun(operation)
    assertResult(())(resultOperation)

  }

  it should "Bracket" in {

    val file: Task[List[String]] = readFileBracket(getURIFileTest(nameFile).getPath)
    val resultFile               = Runtime.default.unsafeRun(file)
    assertResult(List("1 2 3", "4 5 6"))(resultFile)

  }

}
