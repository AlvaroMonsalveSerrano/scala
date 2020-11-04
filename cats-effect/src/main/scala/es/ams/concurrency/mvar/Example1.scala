package es.ams.concurrency.mvar

import cats.effect._
import cats.effect.concurrent._

import scala.concurrent.ExecutionContext

/**
  * https://typelevel.org/cats-effect/concurrency/mvar.html
  *
  * MVar es una es una ubicación mutable que puede estar vacía o contener un valor, bloqueando de forma asíncrona las
  * lecturas cuando estan vacía y bloqueando las escrituras cuando están llenas (productor-consumidor).
  *
  * Los casos de usi:
  * + Como variables mutables sincronizadas y seguras para subprocesos.
  * + Como canales (recibir y enviar)
  * + Como un semáforo binaario (adquirir y liberar)
  */
object Example1 extends App {

  def synchronizedMutableVariable(): Unit = {

    println(s"-*- Example 1-*-")

    implicit val cs = IO.contextShift(ExecutionContext.Implicits.global)

    def sum(state: MVar2[IO, Int], list: List[Int]): IO[Int] = {
      list match {
        case Nil => state.take
        case head :: tail =>
          state.take.flatMap{ current =>
            state.put(current + head).flatMap( _ => sum(state, tail))
          }
      }
    }

    val varProgram: IO[Int] = MVar.of[IO, Int](0).flatMap( current => sum(current, (0 until 100).toList))

    val result = for {
      valueSum <- varProgram
      _ <- IO(println(s"Result=${valueSum}"))
    } yield ()

    result.unsafeRunSync()

  }


  def productorConsumerChannel(): Unit = {

    println(s"-*- Example 2-*-")

    implicit val cs = IO.contextShift(ExecutionContext.Implicits.global)

    type Channel[A] = MVar2[IO, Option[A]]

    def producer(ch: Channel[Int], list: List[Int]): IO[Unit] = {
      list match {
        case Nil => ch.put(None)
        case head :: tail => ch.put(Some(head)).flatMap( _ => producer(ch, tail))
      }
    }

    def consumer(ch: Channel[Int], sum: Long): IO[Long] = {
      ch.take.flatMap{
        case Some(x) => {
          println(s"Consumed=${x}")
          consumer(ch, sum + x)
        }
        case None => IO.pure(sum)
      }
    }

    val program = for {
      channel <- MVar[IO].empty[Option[Int]]
      count = 20
      producerTask = producer(channel, (0 until count).toList )
      consumerTask = consumer(channel, 0L)

      fProducer <- producerTask.start
      fConsumer <- consumerTask.start
      _ <- fProducer.join
      sumResult <- fConsumer.join

    } yield { sumResult }

    val resultProgram = program.unsafeRunSync()
    println(s"Result program=${resultProgram}")

  }

  synchronizedMutableVariable()
  productorConsumerChannel()

}
