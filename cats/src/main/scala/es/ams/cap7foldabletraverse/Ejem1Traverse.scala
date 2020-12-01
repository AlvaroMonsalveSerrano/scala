package es.ams.cap7foldabletraverse

import cats.Applicative

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import cats.syntax.applicative._
import cats.syntax.apply._

//import scala.language.higherKinds

object Ejem1Traverse extends App {

  def getFutureTest(msg: String): Future[Int] = Future{msg.length * 10 }


  /**
    * La función traverse lo que hace es lo siguiente: dada una lista, realiza la acumulación de resultados
    * de toda la iteración de los elementos de la lista.
    *
    * Permite algo como, dado una lista de host y una función de futuros, retorna en un único futuro el resultado
    * de la ejecución de cada host de la lista; permitiendo tratar como un único futuro todas las peticiones
    * a los hosts.
    *
    * @param list
    * @param f
    * @tparam A
    * @tparam B
    * @return
    */
  def myTraverse[A,B](list: List[A])(f: A => Future[B]): Future[List[B]] =
    list.foldLeft(Future(List.empty[B])){
      (acc, elem) => {
        val resultElem = f(elem)
        for{
          acc <- acc
          elem <- resultElem
        }yield{ acc :+ elem }
      }
    }

  // función polimórfica
  def myTraverse2[F[_]: Applicative, A,B](list: List[A])(f: A => F[B]): F[List[B]] =
    list.foldLeft( List.empty[B].pure[F] ){
      (acc, elem) => (acc, f(elem)).mapN(_ :+ _)
    }


  /**
    * Función sequence
    * @param list
    * @tparam B
    * @return
    */
  def mySequence[B](list:List[Future[B]]): Future[List[B]] =
    myTraverse(list)(identity)

  // función polimórfica
  def mySequence2[F[_]:Applicative, B](list: List[F[B]]): F[List[B]] =
    myTraverse2(list)(identity)


// Función definida en el libro de cats. No compila. No sé el por qué
  // Puede que no funcione porque hay que definir el implícito de Applicative
//  def oldCombine(acc: List[Future[Int]], host: String): List[Future[Int]] = {
//    val elemHost: Future[Int] = getFutureTest(host)
//    for{
//      acc <- acc
//      elemHost <- elemHost
//    }yield{ acc :+ elemHost}
//  }


// Función que no compila. No sé el por qué.
  // Puede que no funcione porque hay que definir el implícito de Applicative
//  def newCombine(acc: List[Future[Int]], host: String): List[Future[Int]] =
//    (acc, getFutureTest(host)).mapN( _ :+ _)




  /**
    * Ejemplo con una función tracerse definida a mano.
    */
  def example1(): Unit = {
    val listExample1 = List ("a", "aa", "aaa")
    val resultExample1 = myTraverse(listExample1)(getFutureTest)
    println(s"--Ejemplo1--")
    println(s"myTraverse(List ('a', 'aa', 'aaa'))-->${Await.result( resultExample1, 5.seconds )}")
    println()
  }


  /**
    * Ejemplo con una función sequence.
    *
    */
  def example2(): Unit = {
    val listExampleSequence1 = List( getFutureTest("a"), getFutureTest("aa"), getFutureTest("aaa"))
    val resultExample2 = mySequence(listExampleSequence1)
    println(s"--Ejemplo2--")
    println(s"myTraverse(List (Future('a'), Future('aa'), Future('aaa'))-->${Await.result( resultExample2, 5.seconds )}")
    println()
  }


  def example21(): Unit = {
    import cats.Traverse
    import cats.instances.all._
    val listExampleSequence1 = List( getFutureTest("a"), getFutureTest("aa"), getFutureTest("aaa"))
    val result1:Future[List[Int]] = Traverse[List].sequence(listExampleSequence1)
    println(s"--Ejemplo2_1--")
    println(s"myTraverse(List (Future('a'), Future('aa'), Future('aaa'))-->${Await.result( result1, 5.seconds )}")
    println()
  }

  def example3(): Unit = {
    import cats.instances.option._

    def process(list: List[Int]) = {
      myTraverse2(list)(n => if(n%2==0) Some(n) else None)
    }

    println(s"--Ejemplo3--")
    println(s"process(List(2,4,6))==>>${process(List(2,4,6))}")
    println()
    println(s"process(List(1,2,3))==>>${process(List(1,2,3))}")
    println()
  }


  def example4(): Unit = {

    import cats.data.Validated
    import cats.instances.list._

    type ErrorOn[A] = Validated[List[String] ,A ]

    def process(list: List[Int]): ErrorOn[List[Int]] = {
      myTraverse2(list){ n =>
        if(n%2==0){
          Validated.valid(n)
        }else{
          Validated.invalid(List(s"$n no está incluido."))
        }

      }
    }

    println(s"--Ejemplo4--")
    println(s"process(List(2,4,6))==>>${process(List(2,4,6))}")
    println()
    println(s"process(List(1,2,3))==>>${process(List(1,2,3))}")
    println()
    println(s"process(List(2,4,5,6))==>>${process(List(2,4,5,6))}")
    println()

  }


  def example5(): Unit = {
    import cats.Traverse
    import cats.instances.all._

    println(s"--Ejemplo5--")
    val listExample1 = List ("a", "aa", "aaa")
    val result1:Future[List[Int]] = Traverse[List].traverse(listExample1)(getFutureTest)
    println(s"Traverse1=${Await.result( result1, 2.seconds )}")
    println()

    val listExampleSequence1 = List( Future(1), Future(2), Future(3))
    val result2: Future[List[Int]] = Traverse[List].sequence(listExampleSequence1)
    println(s"Sequence1=${Await.result( result2, 2.seconds )}")
    println()


  }

  // ---

  example1()
  example2()
  example21()
  example3()
  example4()
  example5()

}
