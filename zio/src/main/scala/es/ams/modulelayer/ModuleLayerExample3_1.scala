package es.ams.modulelayer

import zio.{ZIO, ZEnv, App}
import zio.logging._
import zio.random._
import ModuleLayerExample2.{Names, Teams, History}
import es.ams.modulelayer.names._
import es.ams.modulelayer.teams._
import es.ams.modulelayer.history._
import zio.ExitCode

object ModuleLayerExample3_1 extends App{
  
    type MyServices =  Names with Teams with History with Logging
    type Error = Throwable

    // Log layer
    val envLog =
        Logging.console(
        logLevel = LogLevel.Info,
        format = LogFormat.ColoredLogFormat()
        ) >>> Logging.withRootLoggerName("ModuleLayerExample3_1")

    val appEnvironmet = envLog >+> Random.live >+> Names.live >+> Teams.live >+> History.live

    def program(args: List[String]): ZIO[MyServices, Nothing, Boolean] = {
        for{
            _        <- log.info("[START]")
            name     <- randomName
            _        <- log.info(s"Name=${name}")
            lstTeams <- pickTeam(7)
            _        <- log.info(s"List=${lstTeams}")
            result   <- wonLastYear(lstTeams)
            _        <- log.info(s"Result=${result}")
            _        <- log.info(s"[END]")
         } yield result
    }
  

    override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
        (program(List.empty[String]).exitCode).provideCustomLayer(appEnvironmet)
    }
    

}
