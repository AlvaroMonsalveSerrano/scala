package es.ams.basictour

import zio._
import zio.console._
import java.io.IOException

object Example4_1Tour extends zio.App{
  
  val failed =
    putStrLn("About to fail...") *>
      ZIO.fail("Boom") *>
      putStrLn("No printed")

  def program1(): ZIO[ZEnv, Nothing, Int] = {
    (failed as 0) orElse ZIO.succeed(1)
  }

  def program2(): ZIO[ZEnv, Nothing, Int] = {
    failed.fold(_ => 1, _ => 0)
  }

  def program3(): ZIO[ZEnv, IOException, Int] = {
    (failed as 0).catchAllCause(cause => putStrLn(s"Exceptio='${cause.prettyPrint}'") as 1)
  }

  def program(args: List[String]): ZIO[ZEnv, IOException, Int] = {
      args.head match {
          case "1" => program1()
          case "2" => program2()
          case "3" => program3()
          case _ => ZIO.succeed(1)
      }
  }

override def run(args: List[String]): URIO[ZEnv,ExitCode] = program(args).exitCode

}
