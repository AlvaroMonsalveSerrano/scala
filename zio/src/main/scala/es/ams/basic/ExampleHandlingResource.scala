package es.ams.basic

import zio.{Task, UIO}

import java.io.FileInputStream
import scala.io.Source

object ExampleHandlingResource {

  def closeBufferedSource(buffer: FileInputStream) = UIO(buffer.close())

  def readFileBracket(nameFile: String): Task[List[String]] =
    UIO(Source.fromFile(nameFile)).bracket(bufferedSource => UIO(bufferedSource.close())) { file =>
      UIO(file.getLines().toList)
    }

}
