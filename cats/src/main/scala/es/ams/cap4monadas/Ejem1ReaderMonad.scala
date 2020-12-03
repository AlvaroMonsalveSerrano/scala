package es.ams.cap4monadas

/** cats.data.Reader es una mónada que admite una secuencia de operaciones que depende de una entrada.
  *
  * Un ejemplo es la inyección de dependencias
  */
object Ejem1ReaderMonad extends App {

  case class Cat(name: String, favoriteFoods: String)

  /** Ejemlo de creación de una mónada Reader.
    */
  def ejemplo1(): Unit = {

    import cats.data.Reader

    // Creación de un Reader.
    val catName: Reader[Cat, String] = Reader(cat => cat.name)

    // Uso de una Reader.
    println(s"--- EJEMPLO 1: Creación ---")
    println(s"Nombre de gato=${catName.run(Cat("Gatito", "Comida de gatito"))} ")
    println()

  }

  /** Con la función map nos permite componer funciones
    */
  def ejemplo2(): Unit = {

    import cats.data.Reader

    val catName: Reader[Cat, String] = Reader(cat => cat.name)

    val saludo: Reader[Cat, String] = catName.map(name => s"Hola $name")

    println(s"--- EJEMPLO 2: composición de funciones con map---")
    println(s"Composicion función=${saludo.run(Cat("Gato2", "Comida 2"))}")
    println()

  }

  /** Con la función flatMap permite combinar Reader que dependen de algún valor de entrada.
    */
  def ejemplo3(): Unit = {

    import cats.data.Reader

    val catName: Reader[Cat, String] = Reader(cat => cat.name)

    val saludoGato: Reader[Cat, String] = catName.map(name => s"Hola $name")

    val alimentoGato: Reader[Cat, String] = Reader(cat => s"El gato se alimenta de '${cat.favoriteFoods}'")

    // Combinación de mónadas.
    val saludoYalimentoGato: Reader[Cat, String] = for {
      saludo <- saludoGato
      comida <- alimentoGato
    } yield {
      s"saludo=${saludo}. Comida=${comida} "
    }

    println(s"--- EJEMPLO 3: combinaciones de Reader ---")
    println(s"Combinación de Reader=${saludoYalimentoGato(Cat("Gato3", "Comida de gatito3"))}")
    println()

  }

  ejemplo1()
  ejemplo2()
  ejemplo3()

}
