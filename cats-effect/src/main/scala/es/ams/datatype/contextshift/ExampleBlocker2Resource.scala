package es.ams.datatype.contextshift

import java.io.{BufferedReader, File, FileReader}

import cats.effect.{Blocker, ExitCode, IO, IOApp, Resource}

import scala.jdk.CollectionConverters._

/** Ejercicio de lectura de un fichero (Resource) con un blocker.
  *
  * https://typelevel.org/cats-effect/datatypes/resource.html
  * https://typelevel.org/cats-effect/datatypes/contextshift.html
  */
object ExampleBlocker2Resource extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    val fFileSource: File = new File(getClass.getResource("/FileSource.txt").getPath)

    // Definición del contexto con Blocker.
    val name = Blocker[IO].use { blocker =>
      reader(fFileSource, blocker).use { br =>
        readAllLines(br, blocker)
      }
    }

    for {
      lines <- name
      _     <- IO(println(s"Num. lines = ${lines.size}"))
      _     <- IO(println(s"Lines = ${lines}"))
    } yield {
      ExitCode.Success
    }

  }

  def reader(file: File, blocker: Blocker) /*(implicit cs: ContextShift[IO])*/: Resource[IO, BufferedReader] =
    Resource.fromAutoCloseableBlocking(blocker) {
      IO(new BufferedReader(new FileReader(file)))
    }

  def readAllLines(bufferedReader: BufferedReader, blocker: Blocker)
  /*(implicit cs: ContextShift[IO])*/: IO[List[String]] =
    blocker.delay[IO, List[String]] {
      convertIteratorToList(bufferedReader.lines().iterator())
    }

  def convertIteratorToList(stream: java.util.Iterator[String]): List[String] = {
    stream.asScala.toList
  }

}
