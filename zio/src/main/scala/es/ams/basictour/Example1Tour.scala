package es.ams.basictour

import zio._
import zio.console._
import java.io.IOException

object Example1Tour extends zio.App {

  def program(args: List[String]): ZIO[ZEnv, IOException, Int] = {
    // Option 1
    putStrLn("Hello world").map(_ => 0)

    // Option 2
//    putStrLn("Hello world") as 0
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = program(args).exitCode

}
