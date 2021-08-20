package es.ams.basictour

import zio._
import zio.console._

object Example6_1Tour extends zio.App{

  def program(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    (for {
      _    <- putStrLn("Nombre?")
      name <- getStrLn
      _    <- putStrLn(s"Hola ${name}")
    } yield 0) orElse ZIO.succeed(1)
  }

  override def run(args: List[String]): URIO[ZEnv,ExitCode] = program(args).exitCode
  
}
