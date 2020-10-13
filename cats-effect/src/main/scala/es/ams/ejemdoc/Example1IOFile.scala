package es.ams.ejemdoc

import java.io._

import cats.effect.{IO, Resource}
import cats.syntax.all._

//import scala.io.Source

object Example1IOFile extends App {

  /**
    * Operación de copiado.
    *
    * Operador >> (operación de flatMap): Opera con la secuecniia de dos operaciones donde la salida de la primera no es necesaria para
    * la segunda operación. [ first.flatMap(_ => second)) ]
    *
    * @param origin
    * @param destination
    * @param buffer
    * @param acc
    * @return
    */
  def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] =
    for {
      amount <- IO(origin.read(buffer, 0, buffer.size))
      count <- if (amount > -1)
        IO(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
      else
        IO.pure(acc)
    } yield count



  def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
    for {
      buffer <- IO(new Array[Byte](1024 * 10))
      total <- transmit(origin, destination, buffer, 0L)
    } yield total


  def step1AcquiringAndReleasingResources(): Unit = {

    def inputStream(f: File): Resource[IO, FileInputStream] =
      Resource.make {
        IO(new FileInputStream(f)) // build
      } { inStream =>
        IO(inStream.close()).handleErrorWith(_ => IO.unit) // release
      }

    //  Idem
    //  def inputStream(f: File): Resource[IO, FileInputStream] =
    //    Resource.fromAutoCloseable(IO(new FileInputStream(f)))

    def outputStream(f: File): Resource[IO, FileOutputStream] =
      Resource.make {
        IO(new FileOutputStream(f)) // build
      } { outStream =>
        IO(outStream.close()).handleErrorWith(_ => IO.unit) // release
      }

    // Idem
    //  def outputStream(f: File): Resource[IO, FileOutputStream] =
    //    Resource.fromAutoCloseable(IO(new FileOutputStream(f)))

    def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
      for {
        inStream <- inputStream(in)
        outStream <- outputStream(out)
      } yield (inStream, outStream)


    def copy(origin: File, destination: File): IO[Long] =
      inputOutputStreams(origin, destination).use { case (in, out) =>
        transfer(in, out)
      }


    val fFileSource: File = new File(getClass.getResource("/FileSource.txt").getPath)
    val fileTarget: File = new File("FileTarget1.txt")

    val result: IO[Long] = copy(fFileSource, fileTarget)
    println(s"*=== Example step 1 ===*")
    println(s"Resultado 1 =${result.unsafeRunSync()}")

  }

//  def transfer(origin: InputStream, destination: OutputStream): IO[Long] = ???

  /**
    * Resource está basado en bracket. Tiene tres pasos: 1, adquisición de recursos; 2, uso, procesar/operar con recursos;
    * 3, release, liberar recursos.
    *
    * Si hay un problema en el paso 2 (uso) entonces no se ejecuta el paso 3 (release).
    *
    */
  def step2Bracket(): Unit = {

    def copy(origin: File, destination: File): IO[Long] = {
      val inIO: IO[InputStream] = IO(new FileInputStream(origin))
      val outIO: IO[OutputStream] = IO(new FileOutputStream(destination))

      (inIO, outIO)
        .tupled
        .bracket { // paso 2: uso
          case (in, out) => transfer(in, out)

        } { // paso 3: release
          case (in, out) =>{
            IO(in.close()).handleErrorWith( _ => IO.unit)
            IO(out.close()).handleErrorWith( _ => IO.unit)

//            (IO(in.close()), IO(out.close()))
//              .tupled
//              .handleErrorWith(_ => IO.unit).void

          }
        }
    }


    println(s"*=== Example step 2 ===*")

    val fFileSource: File = new File(getClass.getResource("/FileSource.txt").getPath)
    val fileTarget: File = new File("FileTarget2.txt")

    val result: IO[Long] = copy(fFileSource, fileTarget)
    println(s"Resultado 2=${result.unsafeRunSync()}")

  }

  step1AcquiringAndReleasingResources()
  step2Bracket()

}
