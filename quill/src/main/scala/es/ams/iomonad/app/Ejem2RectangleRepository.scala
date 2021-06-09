package es.ams.iomonad.app

import es.ams.iomonad.RectangleRepository

import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Ejem2RectangleRepository extends App {

  import es.ams.macrosamples.domain.Rectangle

  val rectangle1 = Rectangle(id_rec = 100, length_rec = 100, width_rec = 100)

  val repositoryRectangle = new RectangleRepository("asynpostgres")
  val result              = Await.result(repositoryRectangle.insert(rectangle1), Duration.Inf)
  println(s"Result=${result}")

}
