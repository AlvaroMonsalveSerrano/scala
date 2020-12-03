package es.ams.ejemdoc

import java.io._

import cats.effect.concurrent.Semaphore
import cats.effect.{Concurrent, ExitCode, IO, IOApp, Resource}
import cats.syntax.all._

/** Ejemplo con un Semaphore. El hilo que ejecuta un semáforo bloquea al resto sobre el recurso
  * que está utilizando, evitando posibles errores de inconsistencia si se producen errores o
  * cancelaciones de ejecución.
  *
  * La función withPermit realiza la operación de adquisición (.acquire) y liberación de recurso
  * (.release). La función adquiere el permiso sobre el recurso, realiza la operación y libera.
  */
object Example3IOFile extends IOApp {

  def inputStream(f: File, guard: Semaphore[IO]): Resource[IO, FileInputStream] =
    Resource.make {
      IO(new FileInputStream(f)) // build
    } { inStream =>
      guard.withPermit {
        IO(inStream.close()).handleErrorWith(_ => IO.unit) // release
      }
    }

  def outputStream(f: File, guard: Semaphore[IO]): Resource[IO, FileOutputStream] =
    Resource.make {
      IO(new FileOutputStream(f)) // build
    } { outStream =>
      guard.withPermit {
        IO(outStream.close()).handleErrorWith(_ => IO.unit) // release
      }
    }

  def inputOutputStreams(in: File, out: File, guard: Semaphore[IO]): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in, guard)
      outStream <- outputStream(out, guard)
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

  def copy(origin: File, destination: File)(implicit concurrent: Concurrent[IO]): IO[Long] = {
    for {
      guard <- Semaphore[IO](1)
      count <- inputOutputStreams(origin, destination, guard).use { case (in, out) =>
        guard.withPermit(transfer(in, out))
      }
    } yield count

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
