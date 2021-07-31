package es.ams.basictour

import zio.{Runtime, ZEnv, ZIO}
import zio.console._

import java.io.IOException

/** Looping
  */
object Example5Tour extends App {

  def repeat[R, E, A](n: Int)(effect: ZIO[R, E, A]): ZIO[R, E, A] =
    if (n <= 1) effect
    else effect *> repeat(n - 1)(effect)

  def program1(args: List[String]): ZIO[ZEnv, IOException, Int] = {
    repeat(10)(putStrLn("Hello world") as 0)
  }

  val resultProgram1 =
    Runtime.default.unsafeRun(
      program1(List.empty[String]).catchAllCause(cause => (putStrLn(s"Exception=${cause.prettyPrint}") as 1))
    )
  println(s"*1 Result=>${resultProgram1}")

}
