package es.ams.basic

import zio.{ExitCode, URIO}
import zio.console._

/** https://zio.dev/docs/getting_started.html
  */
object Example1Basic extends zio.App {

  val myAppLogic = for {
    _    <- putStrLn("Nombre?")
    name <- getStrLn
    _    <- putStrLn(s"Hi! ${name}")
  } yield ()

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = myAppLogic.exitCode
}
