package es.ams.cap7foldabletraverse

import cats.Eval

object Ejem2Foldable extends App{

  import cats.Foldable
  import cats.instances.all._


  def ejemplo1FoldableFoldLeft(): Unit = {
    println(s"Suma List(1, 2, 3)=${Foldable[List].foldLeft(List(1, 2, 3), 0)( _ + _ )}")
    println(s"Suma Vector(1, 2, 3)=${Foldable[Vector].foldLeft(Vector(1, 2, 3), 0)( _ + _ )}")
    println(s"Suma Stream(1, 2, 3)=${Foldable[Stream].foldLeft(Stream(1, 2, 3), 0)( _ + _ )}")
    println(s"Suma Option(10) + 5=${Foldable[Option].foldLeft(Option(10), 0)( (acc, elem) => elem + 5  )}")
    println
  }


  def ejemplo1FoldableFoldRight(): Unit = {
    val lista = (1 to 100000).toStream

    // OJO!, esta operación no se realiza porque se desborda la pilaStackOverflowError
//    println(s"Suma (1 to 100000).toStream->${ lista.foldRight(0L)(_ + _) }")

    // Solución
    val resultEvalStream = Foldable[Stream].foldRight(lista, Eval.now(0L)) ((num, acc) =>  acc.map( _ + num))
    println(s"Suma (1 to 100000).toStream->${ resultEvalStream.value }")
    println
  }


  def ejemploFoldingMonoids(): Unit = {
    println(s"Foldable[Option].nonEmpty(Option(42))=${Foldable[Option].nonEmpty(Option(42))}"  )
    println(s"Foldable[Option].isEmpty(Option(42))=${Foldable[Option].isEmpty(Option(42))}"  )
    println(s"Foldable[Option].size(Option(42))=${Foldable[Option].size(Option(42))}"  )
    println(s"Foldable[Option].get(Option(42))(0)=${Foldable[Option].get(Option(42))(0) }"  )
    println(s"Foldable[Option].get(Option(42))(1)=${Foldable[Option].get(Option(42))(1) }"  )
    println(s"Foldable[Option].get(Option(42))(2)=${Foldable[Option].get(Option(42))(2) }"  )
    println(s"Foldable[Option].find(Option(42))( elem => elem>30)=${Foldable[Option].find(Option(42))( elem => elem>30) }"  )
    println
    println(s"Foldable[Option].nonEmpty(List(1, 2, 3)=${Foldable[List].nonEmpty(List(1, 2, 3))}"  )
    println(s"Foldable[Option].isEmpty(List(1, 2, 3))=${Foldable[List].isEmpty(List(1, 2, 3))}"  )
    println(s"Foldable[Option].size(List(1, 2, 3)=${Foldable[List].size(List(1, 2, 3))}"  )
    println(s"Foldable[Option].get(List(1, 2, 3)(0)=${Foldable[List].get(List(1, 2, 3))(0)}"  )
    println(s"Foldable[Option].get(List(1, 2, 3)(1)=${Foldable[List].get(List(1, 2, 3))(1)}"  )
    println(s"Foldable[Option].get(List(1, 2, 3)(4)=${Foldable[List].get(List(1, 2, 3))(4)}"  )

    println(s"Foldable[Option].find(List(1, 2, 3)(4)=${Foldable[List].find(List(1, 2, 3))( elem => (elem%2==0) )}"  )
    println(s"Foldable[Option].find(List(1, 2, 3)(4)=${Foldable[List].find(List(1, 2, 3))( elem => (elem%2!=0) )}"  )
    println

    import cats.instances.all._
    println(s"Foldable[Option].combineAll(List(1, 2, 3))=${Foldable[List].combineAll(List(1, 2, 3))}"  )
    println(s"Foldable[List].foldMap(List(1, 2, 3))( elem => elem + 20) =${Foldable[List].foldMap(List(1, 2, 3))( elem => elem + 20) }")
    println
  }


  ejemplo1FoldableFoldLeft()

  ejemplo1FoldableFoldRight()

  ejemploFoldingMonoids()

}
