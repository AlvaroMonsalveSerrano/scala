package es.ams.basictour

import zio._
import zio.console._
import java.io.IOException

object Example5_1Tour extends zio.App{

  def repeat[R, E, A](n: Int)(effect: ZIO[R, E, A]): ZIO[R, E, A] =
    if (n <= 1) effect
    else effect *> repeat(n - 1)(effect)

  def program(args: List[String]): ZIO[ZEnv, IOException, Int] = {
    repeat(10)(putStrLn("Hello world") as 0)
  }


  override def run(args: List[String]): URIO[ZEnv,ExitCode] = program(args).exitCode
  
}
