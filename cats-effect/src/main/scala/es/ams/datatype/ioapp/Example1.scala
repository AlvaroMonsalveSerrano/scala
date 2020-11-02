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
object Example1 extends IOApp{

  def run(args: List[String]): IO[ExitCode] = {
    IO( println("Hola Mundo desde IOApp") ).as(ExitCode.Success)
  }

}
