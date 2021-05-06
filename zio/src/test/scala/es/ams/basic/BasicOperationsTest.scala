package es.ams.basic

import org.scalatest.flatspec.AnyFlatSpec
import zio._

class BasicOperationsTest extends AnyFlatSpec {

  "Basic Operations" should "Mapping" in {

    val succeeded: UIO[Int]  = IO.succeed(21).map(_ * 2)
    val resultSucceeded: Int = Runtime.default.unsafeRun(succeeded)
    assert(42 === resultSucceeded)

  }

  it should "Zipping" in {
    // Combinación de dos efectos en un único efecto.
    // Con la operación zip, el efecto de la izquierda se ejecuta antes que el de la derecha.

    val zipped: UIO[(String, Int)]  = ZIO.succeed("a").zip(ZIO.succeed(2))
    val resultZipped: (String, Int) = Runtime.default.unsafeRun(zipped)
    assert("a" === resultZipped._1)
    assert(2 === resultZipped._2)

  }

}
