package es.ams.basictour

import zio.{Runtime, ZEnv, ZIO}
import zio.console._

object Example6Tour extends App {

  def program1(args: List[String]): ZIO[ZEnv, Nothing, Int] = {
    (for {
      _    <- putStrLn("Nombre?")
      name <- getStrLn
      _    <- putStrLn(s"Hola ${name}")
    } yield 0) orElse ZIO.succeed(1)
  }

  val resultProgram1 = Runtime.default.unsafeRun(program1(List.empty[String]))
  println(s"*1 Result=>${resultProgram1}")

}
