package es.ams.dependencyinyector

import cats.data.Kleisli

import cats.implicits._


/**
  * Kleisli es un wrapper de la función A => M[B]
  */
object Ejem1Kleisli extends App{

  trait Trading[A, B, C, D, E, F]{
    def clientOrder(): Kleisli[List, D, C]
    def execute(m:B, a: A): Kleisli[List, C, E]
    def allocate(as: List[Account]): Kleisli[List, E, F]
  }

  object Trading extends Trading[Account, Market, Order, ClientOrder, Execution, Trade]{
    override def clientOrder(): Kleisli[List, ClientOrder, Order] = Kleisli[List, ClientOrder, Order]  {
      (client: ClientOrder) =>
        List(Order(client.name + " clientOrde"))
    }

    override def execute(m: Market, a: Account): Kleisli[List, Order, Execution] = Kleisli[List, Order, Execution] {
      (order: Order) => {
        List(Execution(order.name + s" execute[Market=${m.name}, a=${a.name}]"))
      }
    }

    override def allocate(as: List[Account]): Kleisli[List, Execution, Trade] = Kleisli[List, Execution, Trade] {
      (execute: Execution) => {
        List(Trade(execute.name + s" Fin comercio[as.length=${as.length}]"))
      }
    }
  }


  //
  // FORMA 1
  //
  def operateWithTrading(): Unit = {
    println(s"-- operateWithTrading() --")
    val objTrading = Trading.clientOrder andThen Trading.execute(Market("Market"), Account("Account"))
    println(s"-->${objTrading.run(ClientOrder("ClienteOrderPrueba"))}")

    val objTrading2 = objTrading andThen Trading.allocate(List( Account("A1"), Account("A2")))
    println(s"-->${objTrading2.run(ClientOrder("ClienteOrderPrueba"))}")
    println

  }


  //
  // FORMA 2
  //
  def compositionFunction(): Unit = {

    def businessFunction1(): Kleisli[List, ClientOrder, Execution] = {
      Trading.clientOrder andThen Trading.execute(Market("Market"), Account("Account"))
    }

    def businessFunction2(): Kleisli[List, ClientOrder, Trade] = {
      businessFunction1() andThen Trading.allocate(List( Account("A1"), Account("A2")))
    }

    println(s"-- compositionFunction() --")
    println(s"-->${businessFunction1.run(ClientOrder("ClienteOrderPrueba"))}")
    println(s"-->${businessFunction2.run(ClientOrder("ClienteOrderPrueba"))}")
    println

  }

  operateWithTrading()
  compositionFunction()

}


