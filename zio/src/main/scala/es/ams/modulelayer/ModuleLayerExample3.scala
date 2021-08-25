package es.ams.modulelayer

import zio.{ZIO, Runtime} 
import zio.logging._
import ModuleLayerExample2.{Names, Teams, History}
import es.ams.modulelayer.names._
import es.ams.modulelayer.teams._
import es.ams.modulelayer.history._

/**
  * Ref: https://timpigden.github.io/_pages/zlayer/Examples.html
  */
object ModuleLayerExample3 extends App{

    type MyServices =  Names with Teams with History with Logging

    // Log layer
    val envLog =
        Logging.console(
        logLevel = LogLevel.Info,
        format = LogFormat.ColoredLogFormat()
        ) >>> Logging.withRootLoggerName("ModuleLayerExample3")

    def program(args: List[String]): ZIO[MyServices, Nothing, Boolean] = {
        for{
            _        <- log.info("[START]")
            name     <- randomName
            _        <- log.info(s"Name=${name}")
            lstTeams <- pickTeam(7)
            _        <- log.info(s"List=${lstTeams}")
            result   <- wonLastYear(lstTeams)
            _        <- log.info(s"[END]")
         } yield result
    }
  
    val result = Runtime.default.unsafeRun(
        program(List.empty[String]).provideCustomLayer(
                envLog ++
                Names.live ++
                (Names.live >>> Teams.live) ++
                (Names.live >>> Teams.live >>> History.live)
            ) 
    )

    println(result)


}
