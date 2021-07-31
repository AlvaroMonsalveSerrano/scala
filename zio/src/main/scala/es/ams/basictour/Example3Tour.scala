package es.ams.basictour

import zio.{ZIO, ZEnv, Runtime}
import zio.console._

import java.io.IOException

/** Sequence of sentences
  */
object Example3Tour extends App {

  def program1(args: List[String]): ZIO[ZEnv, IOException, Int] = {
    putStrLn("1Hello").zipLeft(putStrLn("world")).as(0)
  }

  def program2(args: List[String]): ZIO[ZEnv, IOException, Int] = {
    putStrLn("2Hello").zipRight(putStrLn("world")).as(0)
  }

  def program3(args: List[String]): ZIO[ZEnv, IOException, Int] = {
    (putStrLn("3Hello") *> putStrLn("world")) as 0
  }

  def program4(args: List[String]): ZIO[ZEnv, IOException, Int] = {
    putStrLn("4Hello") *> putStrLn("world") *> ZIO.succeed(0)
  }

  Runtime.default.unsafeRun(program1(List.empty[String]))
  Runtime.default.unsafeRun(program2(List.empty[String]))
  Runtime.default.unsafeRun(program3(List.empty[String]))
  Runtime.default.unsafeRun(program4(List.empty[String]))

}
