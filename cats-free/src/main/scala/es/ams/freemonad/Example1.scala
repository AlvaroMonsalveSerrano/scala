package es.ams.freemonad

import cats.{Id, ~>}

import cats.free.Free
import cats.free.Free.liftF

import cats.data.State

import scala.collection.mutable

/** FreeMomnad
  * ----------
  *
  * https://typelevel.org/cats/datatypes/freemonad.html
  *
  * Una FreeMonad (Free[_]) es una construcción que permite construir una mónada desde cualquier Functor. Es una forma de
  * representar y manipular cálculos.
  *
  * Proporcionan los siguiente:
  *
  * + Representar cálculos con estado.
  * + ejecutar cálculos recursivos de forma segura para la pila.
  * + Construir un DSL integrado.
  * + Reorientar un cálculo a otro intérprete utiliznado transformaciones naturales.
  *
  * Free[_] es un lenguaje de programación dentro del lenguaje. Este lenguaje de programación hay que interpretarlo. Para
  * ello, necesitamos una transformación natural entre contenedores de tipo.
  * Si lo contenedores son F[_] y G[_], la tranformación se escribiría como FunctionK[F, G], simbólicamente F ~> G
  *
  * Free[_] es una estructura recursiva que se puede ver como una secuencia de operaciones que producen otras operaciones.
  * La ejecución se realiza utilizando el morfismo foldMap:
  *
  *   final def foldMap[M[_]](f: FunctionK[S,M])(M: Monad[M]): M[A] = ...
  *
  * Un aspecto importante de foldMap es la seguridad de la pila: evalúa cada paso de cálculo en la pila, desapila y reinicia.
  * Este proceso se conoce como trampolín.
  * La seguridad de la pila proporciona confiabilidad para su uso en tareas de uso intensivo de datos, así como, en
  * procesos infinitos.
  */
object Example1 extends App {

  type KVStore[A] = Free[KVStoreA, A]

  type KVStoreState[A] = State[Map[String, Any], A]

//  def example1(): Unit = {
//
//
//  }

  def pureCompiler: KVStoreA ~> KVStoreState = new (KVStoreA ~> KVStoreState) {

    override def apply[A](fa: KVStoreA[A]): KVStoreState[A] = fa match {

      case Put(key, value) =>
        State.modify(_.updated(key, value))

      case Get(key) =>
        State.inspect(_.get(key).map(_.asInstanceOf[A]))

      case Delete(key) =>
        State.modify(_ - key)

    }
  }

  // Definición del compilador del DSL. Transformación natural impura desde un punto de vista de PF.
  def impureCompiler: KVStoreA ~> Id = new (KVStoreA ~> Id) {

    val kvs = mutable.Map.empty[String, Any]

    def apply[A](fa: KVStoreA[A]): Id[A] = fa match {

      case Put(key, value) =>
        println(s"put($key, $value)")
        kvs(key) = value
        ()
      case Get(key) =>
        println(s"get($key)")
        kvs.get(key).map(_.asInstanceOf[A])

      case Delete(key) =>
        println(s"delete($key)")
        kvs.remove(key)
        ()
    }
  }

  // Definición del DSL integrado ------------------------------
  def delete(key: String): KVStore[Unit] =
    liftF(Delete(key))

  def update[T](key: String, f: T => T): KVStore[Unit] =
    for {
      vMaybe <- get[T](key)
      _      <- vMaybe.map(v => put[T](key, f(v))).getOrElse(Free.pure(()))
    } yield ()

  def put[T](key: String, value: T): KVStore[Unit] =
    liftF[KVStoreA, Unit](Put[T](key, value))

  def get[T](key: String): KVStore[Option[T]] =
    liftF[KVStoreA, Option[T]](Get[T](key))

  // Definición de la gramática -------------------------
  sealed trait KVStoreA[A]

  case class Put[T](key: String, value: T) extends KVStoreA[Unit]

  case class Get[T](key: String) extends KVStoreA[Option[T]]

  case class Delete(key: String) extends KVStoreA[Unit]

  // Parece un flujo monádico pero solo crea una estructura de datos recursiva que representa una secuencia de operaciones.
  def program: KVStore[Option[Int]] =
    for {
      _ <- put("wild-cats", 2)
      _ <- update[Int]("wild-cats", (_ + 12))
      _ <- put("tame-cats", 5)
      n <- get[Int]("wild-cats")
      _ <- delete("tame-cats")
    } yield { n }

  println(s"$program")

  val resultImpure: Option[Int] = program.foldMap(impureCompiler)
  println(s"Result impure =${resultImpure}")

  val resultPure: (Map[String, Any], Option[Int]) = program.foldMap(pureCompiler).run(Map.empty).value
  println(s"Result pure =${resultPure}")

}
