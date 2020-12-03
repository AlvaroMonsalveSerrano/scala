package es.ams.cap10datavalidation

import cats.data.Kleisli
import cats.instances.list._ // for Monad

object Ejem1Kleisli extends App {

  def ejemplo1(): Unit = {

    val step1: Kleisli[List, Int, Int] = Kleisli(x => List(x + 1, x - 1))

    val step2: Kleisli[List, Int, Int] = Kleisli(x => List(x, -x))

    val step3: Kleisli[List, Int, Int] = Kleisli(x => List(x * 2, x / 2))

    println(s"step1=${step1(20)}")
    println(s"step2=${step2(20)}")
    println(s"step3=${step3(20)}")
    println()

    val pipeline1 = step1 andThen step2
    println(s"pipeline1=${pipeline1(20)}")
    println(s"pipeline1_1=${pipeline1.run(20)}")

    val pipeline2 = step1 andThen step2 andThen step3
    println(s"pipeline2=${pipeline2(20)}")
    println(s"pipeline2_1=${pipeline2.run(20)}")
    println()

  }

  ejemplo1()

}
