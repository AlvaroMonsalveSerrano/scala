package es.ams.ejemdoc

import java.io._

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.syntax.all._

/** IOApp es el equivalente a App. Implica definir un método run.
  */
object Example2IOFile extends IOApp {

  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.fromAutoCloseable(IO(new FileInputStream(f)))

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.fromAutoCloseable(IO(new FileOutputStream(f)))

  def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

  def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] =
    for {
      amount <- IO(origin.read(buffer, 0, buffer.size))
      count <-
        if (amount > -1)
          IO(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
        else
          IO.pure(acc)
    } yield count

  def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
    for {
      buffer <- IO(new Array[Byte](1024 * 10))
      total  <- transmit(origin, destination, buffer, 0L)
    } yield total

  def copy(origin: File, destination: File): IO[Long] =
    inputOutputStreams(origin, destination).use { case (in, out) =>
      transfer(in, out)
    }

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <-
        if (args.length < 2)
          IO.raiseError(new IllegalArgumentException("Origin file and/or destination file is empty."))
        else
          IO.unit

      orig = new File(args(0))
      dest = new File(args(1))

      count <- copy(orig, dest)

      _ <- IO(println(s"$count bytes copied from ${orig.getPath} to ${dest.getPath}"))

    } yield ExitCode.Success
  }

}
