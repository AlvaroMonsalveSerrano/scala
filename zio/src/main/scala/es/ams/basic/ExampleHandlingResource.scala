package es.ams.basic

import zio.{Task, UIO}

import java.io.FileInputStream
import scala.io.Source

object ExampleHandlingResource {

  def closeBufferedSource(buffer: FileInputStream): UIO[Unit] = UIO(buffer.close())

  def readFileBracket(nameFile: String): Task[List[String]] =
    // Esquema try/finally. En bracket se define el efecto de la parte de finally
    UIO(Source.fromFile(nameFile)).bracket(bufferedSource => UIO(bufferedSource.close())) { file =>
      UIO(file.getLines().toList)
    }

}
