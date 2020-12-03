package es.ams.cap4monadas

import cats.Eval

object Ejem3EvalMonad extends App {

  //  sealed abstract class MiLista[A]
  sealed trait MiLista[A]

  case class Node[A](elem: A, next: MiLista[A]) extends MiLista[A]

  case class End[A]() extends MiLista[A]

  def miFoldRight[A, B](list: MiLista[A], empty: B)(f: (A, B) => B): B = list match {
    case End()            => empty
    case Node(elem, next) => f(elem, miFoldRight(next, empty)(f))
  }

  val miLista: MiLista[Int] = Node(1, Node(2, Node(3, End())))
  println(s"Lista=$miLista")

  println(s"1 Suma de la lista= ${miFoldRight[Int, Int](miLista, 0)(_ + _)} ")
  println()

  def miFoldRightEval[A, B](list: MiLista[A], empty: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = list match {
    case End()            => empty
    case Node(elem, next) => Eval.defer(f(elem, miFoldRightEval(next, empty)(f)))
  }

  val miLista1 = (1 to 10000).toList
  println(
    s"2 Suma de la lista= ${miFoldRightEval[Int, Int](miLista, Eval.always(0))((a: Int, b: Eval[Int]) => Eval.always(a + b.value)).value} "
  )
  println()

  def miFoldRightEvalList[A, B](list: List[A], empty: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] = list match {
    case Nil          => empty
    case head :: tail => Eval.defer(f(head, miFoldRightEvalList(tail, empty)(f)))
  }

  def mifoldRight[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
    miFoldRightEvalList(as, Eval.now(acc)) { (a, b) =>
      b.map(fn(a, _))
    }.value

  val miLista2 = (1 to 10000000).toList
  println(s"3 Suma de la lista= ${mifoldRight(miLista2, 0L)(_ + _)} ")
  println()

}
