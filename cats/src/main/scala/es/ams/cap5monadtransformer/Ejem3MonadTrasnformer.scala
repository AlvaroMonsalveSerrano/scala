package es.ams.cap5monadtransformer

import cats.data.EitherT
import cats.implicits._


object Ejem3MonadTrasnformer extends App{

  // From A or B to EitherT[F, A, B]
  def example1EitherTConstructor(): Unit = {
    println(s"### From A or B to EitherT[F, A, B] ###")
    val number: EitherT[Option, String, Int] = EitherT.rightT(5)
    println(s"number=${number}")
    println(s"number.value=${number.value}")
    println

    val error: EitherT[Option, String, Int] = EitherT.leftT("Not a number")
    println(s"error=${error}")
    println(s"error.value=${error.value}")
    println
  }

  // From F[A] or F[B] to EitherT[F, A, B]
  def example2EitherTConstructor(): Unit = {
    println(s"### From F[A] or F[B] to EitherT[F, A, B] ###")
    val number0: Option[Int] = Some(5)
    val number: EitherT[Option, String, Int] = EitherT.right(number0)
    println(s"number=${number}")
    println(s"number.value=${number.value}")
    println

    val error0: Option[String] = Some("Not a number")
    val error: EitherT[Option, String, Int] = EitherT.left(error0)
    println(s"error=${error}")
    println(s"error.value=${error.value}")
    println
  }

  // From Either[A, B] or F[Either[A, B]] to EitherT[F, A, B]
  def example3EitherTConstructor(): Unit = {
    println(s"From Either[A, B] or F[Either[A, B]] to EitherT[F, A, B]")

    val NumberE: Either[String, Int] = Right(100)
    val numberET: EitherT[List, String, Int] = EitherT.fromEither(NumberE)
    println(s"number=${numberET}")
    println(s"number.value=${numberET.value}")
    println

    val errorE: Either[String, Int] = Left("Not a number")
    val errorET: EitherT[List, String, Int] = EitherT.fromEither(errorE)
    println(s"errorET=${errorET}")
    println(s"errorET.value=${errorET.value}")
    println

    val numberFE: List[Either[String, Int]] = List(Right(250))
    val numberFET: EitherT[List, String, Int] = EitherT(numberFE)
    println(s"numberFET=${numberFET}")
    println(s"numberFET.value=${numberFET.value}")
    println
  }


  example1EitherTConstructor()
  example2EitherTConstructor()
  example3EitherTConstructor()
//  example4EitherTConstructor()
}
