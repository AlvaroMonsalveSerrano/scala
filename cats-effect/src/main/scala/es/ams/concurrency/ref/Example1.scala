package es.ams.concurrency.ref

import cats.effect.{IO, Sync}
import cats.effect.concurrent.Ref
import cats.syntax.all._
import scala.concurrent.ExecutionContext

/** https://typelevel.org/cats-effect/concurrency/ref.html
  *
  * Ref es una referencia mutable concurrente y asíncrona.
  *
  * Proporciona acceso simultáneo seguro y modificación de su contenido pero sin funcionalidad de sincronización.
  *
  * Siempre debe de ser inicializada.
  *
  * El caso de uso más común es el contador concurrente. Sean varias procesos Worker que ejecutarán y modificarán  el
  * valor de Ref
  */
object Example1 extends App {

  def concurrentCounter(): Unit = {

    implicit val cs = IO.contextShift(ExecutionContext.Implicits.global)

    class Worker[F[_]](number: Int, ref: Ref[F, Int])(implicit F: Sync[F]) {

      private def putStrLn(value: String): F[Unit] = F.delay(println(value))

      def start: F[Unit] =
        for {
          c1 <- ref.get
          _  <- putStrLn(show"#${number} >> ${c1}")
          c2 <- ref.modify(x => (x + 1, x))
          _  <- putStrLn(show"#${number} >> ${c2}")

        } yield {}
    }

    val program: IO[Unit] =
      for {
        ref <- Ref.of[IO, Int](0)
        worker1 = new Worker[IO](1, ref)
        worker2 = new Worker[IO](2, ref)
        worker3 = new Worker[IO](3, ref)

        _ <- List(
          worker1.start,
          worker2.start,
          worker3.start
        ).parSequence.void
      } yield {}

    program.unsafeRunSync()

  }

  concurrentCounter()

}
