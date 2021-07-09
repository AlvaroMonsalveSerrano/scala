package es.ams.logging

import zio._
import zio.logging._

/** Level error.
  *
  * trace - 600 LogLevel.Trace
  * debug - 500 LogLevel.Debug
  * info - 400 LogLevel.Info
  * warn - 300 LogLevel.Warn
  * error - 200 LogLevel.Error
  */
object Ejem3 extends zio.App {

  val env =
    Logging.console(
      logLevel = LogLevel.Trace,
      format = LogFormat.ColoredLogFormat()
    ) >>> Logging.withRootLoggerName("Ejem3-ZIO-logging")

  def function1(): UIO[Int] = IO.succeed(5)

  def function2(): UIO[String] = IO.succeed("Function2 message")

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    (
      for {
        _       <- log.trace(s"Trace message")
        _       <- log.debug(s"Debug message")
        valFun1 <- function1()
        _       <- log.info(s"Info message valFun1=${valFun1}")
        valFun2 <- function2()
        _       <- log.warn(s"Warnning message valFun2=${valFun2}")
        _       <- log.error(s"Error message")
      } yield { ExitCode.success }
    ).provideLayer(env)

  }

}
