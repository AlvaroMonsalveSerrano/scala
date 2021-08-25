package es.ams.modulelayer

import zio.{ ZIO, ZEnv, App} // ZIO, ZEnv,
import zio.logging._
import zio.ExitCode
import zio.URIO

import zio.console._

import ModuleLayerExample4Module.{Extractor, Transformer, Loader}

import es.ams.modulelayer.extractor._
import es.ams.modulelayer.transformer._
import es.ams.modulelayer.loader._


object ModuleLayerExample4 extends App{

    type Services = Extractor with Transformer with Loader with Logging
  
    // Log layer
    val envLog =
        Logging.console(
        logLevel = LogLevel.Info,
        format = LogFormat.ColoredLogFormat()
        ) >>> Logging.withRootLoggerName("ModuleLayerExample4")

    val appEnvironment = envLog >+> Extractor.live >+> Transformer.live >+> Loader.live

    def program(): ZIO[Services, Throwable, Boolean] = {
        (for {
            _                  <- log.info("[START]")
            dataExtracted      <- extractData
            _                  <- log.info(s"[extrated done] data = ${dataExtracted}")
            dataTransformed    <- transformer(dataExtracted)
            _                  <- log.info(s"[transformed done] data = ${dataTransformed}")
            dataLoaded         <- loader(dataTransformed)
            _                  <- log.info(s"[loaded done] data = ${dataLoaded}")
            _                  <- log.info(s"[END]")

         } yield { true }
        ) orElse ZIO.succeed(false)
    }

    override def run(args: List[String]): URIO[ZEnv,ExitCode] = {
        (program().catchAllCause(cause => putStrLn(s"Exception=${cause.prettyPrint}")).exitCode)
            .provideCustomLayer(appEnvironment)
            
            
    }

}
