package es.ams.cap3functors

object InvariantCatsEjem1 extends App {

  // Ejemplo de Invariant de functores. Invariant: functores bidireccionales.
  def ejemplo1(): Unit = {

    import cats.Monoid
    import cats.instances.all._
    import cats.syntax.invariant._
    import cats.syntax.semigroup._
    import scala.Symbol // Equivale a un String

    implicit val symbolMonoid: Monoid[Symbol] =
      Monoid[String].imap( (nombre:String) => Symbol.apply(nombre) )( (elem:Symbol) => elem.name)
//    Monoid[String].imap( Symbol.apply )( elem.name )

    val monoidSymbol = Monoid[Symbol].empty

    println(s"monoidSymbol=${ monoidSymbol } ")
    println
    println(s"Símbolo básico=${ 'SymbolEjem1 } ")
    println

    val symbolFromString = 'SymbolEjem1 |+| 'paraProbar |+| 'cosasInvariantes
    println(s"symbolFromString= ${ symbolFromString } ")
    println(s"getClass= ${ symbolFromString.getClass } ")
    println

    println(s"monoidSymbol=${ "stringToImap" |+| "Imap"} ")
    println

  }

  ejemplo1()

}
