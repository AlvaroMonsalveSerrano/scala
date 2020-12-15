package es.ams.basic

//import zio.{Fiber, UIO}

object ExampleBasicConcurrency {
//
//  def fib(n: Long): UIO[Long] = {
//    if (n <= 1) UIO.succeed(n)
//    else fib(n - 1).zipWith(fib(n - 2))(_ + _)
//  }.flatten
//
//  def fib100Fiber: UIO[Fiber[Nothing, Long]] =
//    for {
//      fiber <- fib(100).fork // Create a fiber
//    } yield fiber
}
