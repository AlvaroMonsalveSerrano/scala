package es.ams.concurrency.semaphore

import cats.effect.{Concurrent, IO, Timer}
import cats.effect.concurrent.Semaphore
import cats.syntax.all._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * https://typelevel.org/cats-effect/concurrency/semaphore.html
  * Un semáforo tiene un número de permisos no negativo disponibles. La adquisición (acquire) de un permiso, redure el
  * número total de permisos disponibles, la libreración (release) aumenta el número de permisos disponibles.
  * La adquisición cunado no hay permisos produce un bloqueo semántico hasta que esté disponible.
  *
  * El semáforo hace un bloqueo semántico el cual consiste en no bloquear a los subprocesos reales, se espera hasta
  * tener permiso.
  *
  * El caso de uso es cuando varios procesos pretenden acceder a un recurso valioso y es posible restringir el número de
  * accesos.
  *
  *
  */
object Example1 extends App {

  def semaphore(): Unit = {

    implicit val cs = IO.contextShift(ExecutionContext.global)

    implicit val timer = IO.timer(ExecutionContext.global)

    class PreciousResource[F[_]](name: String, s: Semaphore[F])(implicit F: Concurrent[F], timer: Timer[F]){
      def use: F[Unit] = {
        for{
          x <- s.available // Retorna el número de permisos disponibles.
          _ <- F.delay( println(s"$name >> Availability: $x") )
          _ <- s.acquire

          y <- s.available
          _ <- F.delay( println(s"$name >> Started | Availability: $y") )
          _ <- timer.sleep(3.seconds)
          _ <- s.release

          z <- s.available
          _ <- F.delay( println(s"$name >> Done | Avalilability: $z") )
        }yield {}
      }
    }

    val program: IO[Unit] =
      for{
        semaphore <- Semaphore[IO](1) // Semáforo con un permiso.
                     r1 = new PreciousResource[IO]("R1", semaphore)
                     r2 = new PreciousResource[IO]("R2", semaphore)
                     r3 = new PreciousResource[IO]("R3", semaphore)

        _ <- List(r1.use, r2.use, r3.use).parSequence.void
      } yield {}

      program.unsafeRunSync()

  }

  semaphore()

}
