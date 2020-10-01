package es.ams.cap3functors

import cats.Functor

/**
  * Definición de un Functor transformador a partir del API Functor de Cats.
  * Se define un type class para su implementación.
  *
  */
object MyFunctor extends MyFunctorInstances with MyFunctorSintaxys

trait MyFunctorInstances{
  def apply[F[_]](implicit F: Functor[F]): Functor[F] = F

  implicit val functorList = new Functor[List] {
    override def map[A, B](fa: List[A])(f: A => B): List[B] = fa.map(f)
  }

  implicit val functorOption = new Functor[Option] {
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa.map(f)
  }
}

trait MyFunctorSintaxys{
  object syntax{

    implicit class MyFunctorOps[F[_],A](elem: F[A])(implicit F:Functor[F]){
      def transformador[B](f: A => B): F[B] = F.map(elem)(f)
    }

  }
}


