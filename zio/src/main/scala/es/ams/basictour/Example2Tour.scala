package es.ams.basictour

import zio.{ZIO, ZEnv, Runtime}
import zio.console._

import java.io.IOException

object Example2Tour extends App {

  def program(args: List[String]): ZIO[ZEnv, IOException, Int] = {
    // Option 1
    putStrLn("Hello world").map(_ => 0)

    // Option 2
    //    putStrLn("Hello world") as 0
  }

  Runtime.default.unsafeRun(program(List.empty[String]))

}
