package es.ams.datatype.syncio

import cats.effect.SyncIO

/**
  * SyncIO
  * ======
  *
  * SyncIo es una abstracción que representa la intención de realizar un efecto de lado donde el resultado del efecto
  * se obtiene de forma síncrona.
  *
  * SyncIO es similar a IO pero no admite procesamiento asíncrono. La forma de obtener un resultado de un efecto es
  * a través de unsafeRunSync. Es diferente a IO#unsafeRunSync.
  *
  * https://typelevel.org/cats-effect/datatypes/syncio.html
  *
  */
object Example1 extends App{

  // Constructing SyncIO values
  def example1(): Unit = {

    println(s"-*- Example1 -*-")

    def putStrLn(str: String): SyncIO[Unit] = SyncIO( println(str) )

    SyncIO.pure("Cat!").flatMap(putStrLn).unsafeRunSync()

  }

  // Interoperation with Eval and IO
  def example2(): Unit = {
    println(s"-*- Example2 -*-")

    import cats.Eval

    val eval = Eval.now("Hi!")

    val result = SyncIO.eval(eval).unsafeRunSync()
    println(s"${result}")

  }

  /**
    * Interoperation with Eval and IO
    *
    * SyncIO define to[F] para realizar transformaciones.
    *
    */
  def example3(): Unit = {

    import cats.effect.IO

    println(s"-*- Example3 -*-")

    val ioa: SyncIO[Unit] = SyncIO(println("Hello world!"))

    val iob: IO[Unit] = ioa.to[IO]

    iob.unsafeRunAsync( _ => () )

  }

  example1()
  example2()
  example3()


}
