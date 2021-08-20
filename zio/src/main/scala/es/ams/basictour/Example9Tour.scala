package es.ams.basictour

import zio._
import zio.logging._

/**
  * Simple console log.
  */
object Example9Tour extends zio.App{

    val env =
        Logging.console(
        logLevel = LogLevel.Info,
        format = LogFormat.ColoredLogFormat()
        ) >>> Logging.withRootLoggerName("my-component")

    override def run(args: List[String]): URIO[ZEnv,ExitCode] = 
        log.info("Hello from ZIO logger...").provideCustomLayer(env).as(ExitCode.success)
  
}
