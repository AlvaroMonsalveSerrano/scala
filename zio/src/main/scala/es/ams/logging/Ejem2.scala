package es.ams.logging

import zio._
import zio.logging._

/**
  */
object Ejem2 extends zio.App {

  val env = Logging.consoleErr()

  override def run(args: List[String]) = {
    log
      .locally(LogAnnotation.Name("Ejem2" :: Nil)) {
        log.debug("Mensaje debug")
        log.info("Hello from ZI logger")
        log.error("Error message")
      }
      .provideCustomLayer(env)
      .as(ExitCode.success)

  }

}
