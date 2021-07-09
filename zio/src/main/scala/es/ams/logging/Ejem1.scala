package es.ams.logging

import zio.{ExitCode, URIO}
import zio.logging._

object Ejem1 extends zio.App {

  val env =
    Logging.console(
      logLevel = LogLevel.Info,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("Ejem1-ZIO-logging")

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    log.info("Hello from ZI logger").provideCustomLayer(env).as(ExitCode.success)

  }
}
