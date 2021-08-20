package es.ams.basictour

import zio._
import zio.console._
import zio.random._
import zio.logging._

object Example8_1Tour extends zio.App{

    val env = Logging.console(
            logLevel = LogLevel.Info,
            format = LogFormat.ColoredLogFormat()
        ) >>> Logging.withRootLoggerName("Example8_1Tour")

    def analyzeAnswer(random: Int, guess: String) = {
        if(random.toString() == guess.trim()){
            putStrLn(s"You guessed correctly")
        }else{
            putStrLn(s"You did not guess correctly, The answer was ${random}")
        }
    }

    def program(args: List[String]): ZIO[ZEnv with Logging, Nothing, Int] = {
        (for{
            _       <- log.info("[START]")
            random  <- nextIntBetween(0,3)
            _       <- putStrLn("Please guess a number 0 to 3:")
            guess   <- getStrLn
            _       <- analyzeAnswer(random, guess)
            _       <- log.info("[END]")
        } yield 0) orElse ZIO.succeed(1)
    }
  
    override def run(args: List[String]): URIO[ZEnv,ExitCode] = (program(args).exitCode).provideCustomLayer(env)

}
