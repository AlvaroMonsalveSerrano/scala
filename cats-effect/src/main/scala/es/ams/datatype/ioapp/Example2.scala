package es.ams.datatype.ioapp

import cats.effect.{ExitCode, IO, IOApp}

/**
  * IO Data Type
  * ============
  *
  * Pure Programs
  *
  * https://typelevel.org/cats-effect/datatypes/ioapp.html
  *
  */
object Example2 extends IOApp{

  def run(args: List[String]): IO[ExitCode] = {
    args.headOption match {
      case Some(name) =>
        IO(println(s"Hello, $name.")).as(ExitCode.Success)
      case None =>
        IO(System.err.println("Usage: MyApp name")).as(ExitCode(2))
    }
  }

}
