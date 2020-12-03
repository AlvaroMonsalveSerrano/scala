package es.ams.cap9mapreduce

import cats.{Foldable, Monoid}
import cats.instances.all._
import cats.syntax.all._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
//import scala.language.postfixOps
import akka.util.Timeout

object Ejem1MapReduce extends App {

  implicit val timeout = Timeout(2 seconds)

  //
  // Ejemplos de implementación de la operación de transformación.
  //
  /** Ejemplo encapsulando Foldable en una función
    */
  def ejemplo1(): Unit = {
    def foldMap[A, B](inputs: Vector[A])(f: A => B)(implicit monoid: Monoid[B]): B = {
      Foldable[Vector].foldMap(inputs)(f)
    }

    def intToString(elem: Int): String = elem.toString
    def stringToInt(elem: String): Int = elem.toInt

    println(s"-- Ejemplo1 --")
    val dataInputInt = Vector(1, 2, 3, 4)
    println(s"FoldMap=${foldMap(dataInputInt)(intToString)}")
    println()

    val dataInputString = Vector("1", "2", "3", "4")
    println(s"FoldMap=${foldMap(dataInputString)(stringToInt)}")
    println()
  }

  /** Utilizando directamente Foldable y un Monoide.
    * Se aplican las funciones combine del monoide en función del tipo de entrada.
    */
  def ejemplo2(): Unit = {
    println(s"-- Ejemplo2 --")
    val dataInputInt = Vector(1, 2, 3, 4)
    println(s"FolMap Foldable=${Foldable[Vector].foldMap(dataInputInt)(identity)}")
    println()
    val dataInputString = Vector("1", "2", "3", "4")
    println(s"FoldMap Foldable=${Foldable[Vector].foldMap(dataInputString)(identity)}")
    println()
    val dataFromString: Vector[Char] = "Esto es un mensaje desde un String".toVector
    println(
      s"FoldMap Foldable=${Foldable[Vector].foldMap(dataFromString)((elem: Char) => elem.toString.toUpperCase())}"
    )
    println()
  }

  /** Definición de un foldMap con un Context Bound de tipo Monoide. Se opera con Monoid y semigroupal
    */
  def ejemplo3(): Unit = {
    def myFoldMap[A, B: Monoid](inputs: Vector[A])(f: A => B): B = {
      inputs.foldLeft(Monoid[B].empty)((acc, elem) => (acc |+| f(elem)))
    }

    println(s"-- Ejemplo3 --")
    val dataInputInt = Vector(1, 2, 3, 4)
    println(s"FolMap Foldable=${myFoldMap(dataInputInt)((elem: Int) => elem.toString)}")
    println()
    val dataInputString = Vector("1", "2", "3", "4")
    println(s"FolMap Foldable=${myFoldMap(dataInputString)((elem: String) => elem.toInt)}")
    println()
  }

  /** Visualización del número de procesadores existentes en la máquina.
    *
    * Sirve para conocer el número de paquetes a dividir la estructura de datos de entrada
    * y poder realizar el procesamiento paralelo con Futuros.
    *
    * Cada procesador procesará un Future con unos datos de entrada.
    */
  def ejemplo4(): Unit = {
    println(s"-- Ejemplo4 --")
    println(s"Número de procesadores=${Runtime.getRuntime.availableProcessors}")
    println()
  }

  /** Ejemplo de partición de una colección de datos.
    *
    * Se agrupan las colecciones en grupo de tres.
    */
  def ejemplo5(): Unit = {
    println(s"-- Ejemplo5 --")
    val resultListGrouped = (1 to 10).toList.grouped(3).toList
    println(s"Lista de datos particionados en 3(List)=${resultListGrouped}")
    val resultVectorGrouped = (1 to 10).toVector.grouped(3).toList
    println(s"Lista de datos particionados en 3(Vector)=${resultVectorGrouped}")
    val resultVectorGroupedRuntime = (1 to 10).toVector.grouped(Runtime.getRuntime.availableProcessors).toList
    println(s"Lista de datos particionados en 3(Vector)=${resultVectorGroupedRuntime}")
    println()
  }

  /** Implementación de una función fold con ejecución paralela.
    */
  def ejemplo6(): Unit = {

    def parallelFoldMap[A, B: Monoid](inputs: Vector[A])(f: A => B): Future[B] = {
      val numCores   = Runtime.getRuntime.availableProcessors
      val groupSizes = (1.0 * inputs.size / numCores).ceil.toInt

      println(s"Número de cores=${numCores}")
      println(s"Tamaño de grupo=${groupSizes}")

      val groups: Iterator[Vector[A]] = inputs.grouped(groupSizes)

      // Creación de un Future para cada grupo con un foldMap.
      val futures: Iterator[Future[B]] = groups.map { group =>
        Future {
          group.foldLeft(Monoid[B].empty)((acc, elem) => acc |+| f(elem))
        }
      }

      // FoldMap para calculas el resultado.
      Future.sequence(futures).map { iterable =>
        iterable.foldLeft(Monoid[B].empty)(_ |+| _)
      }
    }

//    val result: Future[Int] = parallelFoldMap( (1 to 1000000).toVector )(identity)
    val result: Future[Int] = parallelFoldMap((1 to 10).toVector)(identity)

    println(s"-- Ejemplo6 --")
    println(s"Result Map-Reduce=${Await.result(result, 1.second)}")
    println()

  }

  /** Implementación de una función fold con ejecución paralela.
    */
  def ejemplo7(): Unit = {

    def parallelFoldMap[A, B: Monoid](inputs: Vector[A])(f: A => B): Future[B] = {
      val numCores   = Runtime.getRuntime.availableProcessors
      val groupSizes = (1.0 * inputs.size / numCores).ceil.toInt

// 1 forma.
//      inputs
//        .grouped(groupSizes)
//        .toVector
//        .traverse(group => Future(group.toVector.foldMap(f)))
//        .map(_.combineAll)

// 2-forma.
      val groups: Vector[Vector[A]]  = inputs.grouped(groupSizes).toVector
      val futures: Future[Vector[B]] = groups.traverse(group => Future(group.toVector.foldMap(f)))
      val result: Future[B]          = futures.map(_.combineAll)
      result

    }

    //    val result: Future[Int] = parallelFoldMap( (1 to 1000000).toVector )(identity)
    val result: Future[Int] = parallelFoldMap((1 to 10).toVector)(identity)

    println(s"-- Ejemplo7 --")
    println(s"Result Map-Reduce=${Await.result(result, 1.second)}")
    println()

  }

  ejemplo1()
  ejemplo2()
  ejemplo3()
  ejemplo4()
  ejemplo5()
  ejemplo6()
  ejemplo7()

}
