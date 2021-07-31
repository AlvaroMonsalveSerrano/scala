package es.ams.basictour

import zio.{Runtime, ZEnv, ZIO}
import zio.console._

import java.io.IOException

object Example4Tour extends App {

  val failed =
    putStrLn("About to fail...") *>
      ZIO.fail("Boom") *>
      putStrLn("No printed")

  def program1(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    (failed as 0) orElse ZIO.succeed(1)
  }

  def program2(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    failed.fold(_ => 1, _ => 0)
  }

  def program3(args: List[String]): ZIO[ZEnv, IOException, Int] = {
    (failed as 0).catchAllCause(cause => putStrLn(s"Exceptio=${cause.prettyPrint}") as 1)
  }

  val resultProgram1 = Runtime.default.unsafeRun(program1(List.empty[String]))
  println(s"*1 Result=>${resultProgram1}")

  val resultProgram2 = Runtime.default.unsafeRun(program2(List.empty[String]))
  println(s"*2 Result=>${resultProgram2}")

  val resultProgram3 = Runtime.default.unsafeRun(program3(List.empty[String]))
  println(s"*3 Result=>${resultProgram3}")

}
