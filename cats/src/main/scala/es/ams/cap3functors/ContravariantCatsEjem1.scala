package es.ams.cap3functors



object ContravariantCatsEjem1 extends App {

  // Ejemplos de Contramap
  def ejemplo1(): Unit = {
    import cats.Show
    import cats.functor.Contravariant
    import cats.instances.all._

    val showString = Show[String] // Definición de la categoría? inicial
    val showInt = Show[Int] // Definición de la categoría? inicial

    println(s"-*-*- EJEMPLOS DE CONTRAMAP SIN SINTAXIS -*-*-")
    println(s"showString=${showString.show("Ejemplo de Show[String]")}")
    println

    println(s"showInt=${showInt.show(69)}")
    println

    case class Symbol(name:String, numero:Int)

    val showNameSymbol = Contravariant[Show].contramap(showString)((symbol:Symbol) => s"${symbol.name}" )
    println(s" Contramap Name Show=${showNameSymbol.show( Symbol("Martín", 7))} ")
    println
    println(s" Contramap Name Show=${showNameSymbol.show( Symbol("Martín", 7))} ")
    println
    println(s" Contramap Name Show=${showNameSymbol.show( Symbol("Natalia", 11)  )} ")
    println

    val showNumeroSymbol = Contravariant[Show].contramap(showString)( (symbol:Symbol) => s"${symbol.numero}" )
    println(s" Contramap Numero Show=${showNumeroSymbol.show( Symbol("Martín", 7))} ")
    println
    println(s" Contramap Numero Show=${showNumeroSymbol.show( Symbol("Martín", 7))} ")
    println
    println(s" Contramap Numero Show=${showNumeroSymbol.show( Symbol("Natalia", 11)  )} ")
    println

  }

  def ejemplo2(): Unit = {

    import cats.Show
    import cats.instances.all._
    import cats.syntax.contravariant._

    case class Symbol(name:String, numero:Int)

    val showString = Show[String] // Definición de la categoría? inicial

    val showName   = showString.contramap[Symbol]( (elem:Symbol) => elem.name )
    val showNumero = showString.contramap[Symbol]( (elem:Symbol) => elem.numero.toString )

    println(s"-*-*- EJEMPLOS DE CONTRAMAP CON SINTAXIS -*-*-")
    println(s"Muestra name=${showName.show(Symbol("Martin",7))}")
    println
    println(s"Muestra numero=${showNumero.show(Symbol("Martin",7))}")
    println

  }


  ejemplo1()
  ejemplo2()

}
