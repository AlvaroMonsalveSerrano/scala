package es.ams.dependencyinyector

import cats.data.Kleisli

import cats.implicits._

//case class Account (name: String)
//case class Market (name: String)
//case class Order (name: String)
//case class ClientOrder (name: String)
//case class Execution (name: String)
//case class Trade (name: String)

/** Kleisli es un wrapper de la función A => M[B]
  */
object Ejem2Kleisli extends App {

  // TAccount, TMarket, TOrder, TClientOrder, TExecution, TTrade
  trait Trading2Generic[M[_], A, B, C, D, E, F] {
    def clientOrder(): Kleisli[M, D, C]
    def execute(m: B, a: A): Kleisli[M, C, E]
    def allocate(as: List[Account]): Kleisli[M, E, F]
  }

  object Trading2GenericList extends Trading2Generic[List, Account, Market, Order, ClientOrder, Execution, Trade] {
    override def clientOrder(): Kleisli[List, ClientOrder, Order] = Kleisli[List, ClientOrder, Order] {
      (client: ClientOrder) =>
        List(Order(client.name + " clientOrde"))
    }

    override def execute(m: Market, a: Account): Kleisli[List, Order, Execution] = Kleisli[List, Order, Execution] {
      (order: Order) =>
        {
          List(Execution(order.name + s" execute[Market=${m.name}, a=${a.name}]"))
        }
    }

    override def allocate(as: List[Account]): Kleisli[List, Execution, Trade] = Kleisli[List, Execution, Trade] {
      (execute: Execution) =>
        {
          List(Trade(execute.name + s" Fin comercio[as.length=${as.length}]"))
        }
    }
  }

  object Trading2GenericOption extends Trading2Generic[Option, Account, Market, Order, ClientOrder, Execution, Trade] {
    override def clientOrder(): Kleisli[Option, ClientOrder, Order] = Kleisli[Option, ClientOrder, Order] {
      (client: ClientOrder) =>
        Option(Order(client.name + " clientOrde"))
    }

    override def execute(m: Market, a: Account): Kleisli[Option, Order, Execution] = Kleisli[Option, Order, Execution] {
      (order: Order) =>
        {
          Option(Execution(order.name + s" execute[Market=${m.name}, a=${a.name}]"))
        }
    }

    override def allocate(as: List[Account]): Kleisli[Option, Execution, Trade] = Kleisli[Option, Execution, Trade] {
      (execute: Execution) =>
        {
          Option(Trade(execute.name + s" Fin comercio[as.length=${as.length}]"))
        }
    }
  }

  //
  // FORMA 1
  //
  def operateWithTrading2List(): Unit = {
    println(s"-- operateWithTrading2List() --")
    val objTrading = Trading2GenericList.clientOrder() andThen
      Trading2GenericList.execute(Market("Market"), Account("Account"))
    println(s"-->${objTrading.run(ClientOrder("ClienteOrderPrueba"))}")

    val objTrading2 = objTrading andThen Trading2GenericList.allocate(List(Account("A1"), Account("A2")))
    println(s"-->${objTrading2.run(ClientOrder("ClienteOrderPrueba"))}")
    println()
  }

  def operateWithTrading2Option(): Unit = {
    println(s"-- operateWithTrading2Option() --")
    val objTrading = Trading2GenericOption.clientOrder() andThen
      Trading2GenericOption.execute(Market("Market"), Account("Account"))
    println(s"-->${objTrading.run(ClientOrder("ClienteOrderPrueba"))}")

    val objTrading2 = objTrading andThen Trading2GenericOption.allocate(List(Account("A1"), Account("A2")))
    println(s"-->${objTrading2.run(ClientOrder("ClienteOrderPrueba"))}")
    println()
  }

  //
  // FORMA 2
  //
  def compositionFunction2List(): Unit = {
    def businessFunction1(): Kleisli[List, ClientOrder, Execution] = {
      Trading2GenericList.clientOrder() andThen Trading2GenericList.execute(Market("Market"), Account("Account"))
    }

    def businessFunction2(): Kleisli[List, ClientOrder, Trade] = {
      businessFunction1() andThen Trading2GenericList.allocate(List(Account("A1"), Account("A2")))
    }

    println(s"-- compositionFunction2List() --")
    println(s"-->${businessFunction1().run(ClientOrder("ClienteOrderPrueba"))}")
    println(s"-->${businessFunction2().run(ClientOrder("ClienteOrderPrueba"))}")
    println()
  }

  def compositionFunction2Option(): Unit = {
    def businessFunction1(): Kleisli[Option, ClientOrder, Execution] = {
      Trading2GenericOption.clientOrder() andThen Trading2GenericOption.execute(Market("Market"), Account("Account"))
    }

    def businessFunction2(): Kleisli[Option, ClientOrder, Trade] = {
      businessFunction1() andThen Trading2GenericOption.allocate(List(Account("A1"), Account("A2")))
    }

    println(s"-- compositionFunction2Option() --")
    println(s"-->${businessFunction1().run(ClientOrder("ClienteOrderPrueba"))}")
    println(s"-->${businessFunction2().run(ClientOrder("ClienteOrderPrueba"))}")
    println()
  }

  operateWithTrading2List()
  operateWithTrading2Option()

  compositionFunction2List()
  compositionFunction2Option()
}
