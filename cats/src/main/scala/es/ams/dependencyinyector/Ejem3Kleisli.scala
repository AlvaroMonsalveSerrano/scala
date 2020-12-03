package es.ams.dependencyinyector

import TypeEjem1Kleisli.{TAccount, TClientOrder, TExecution, TMarket, TOrder, TTrade}
import cats.data.Kleisli
import cats.implicits._

/** Kleisli es un wrapper de la función A => M[B]
  */
object Ejem3Kleisli extends App {

  // TAccount, TMarket, TOrder, TClientOrder, TExecution, TTrade
  trait Trading3Generic[M[_]] {
    def clientOrder(): Kleisli[M, TClientOrder, TOrder]
    def execute(m: TMarket, a: TAccount): Kleisli[M, TOrder, TExecution]
    def allocate(as: List[Account]): Kleisli[M, TExecution, TTrade]
  }

  object Trading3Generic extends Trading3GenericInstances with Trading3GenericInstancesSyntax

  trait Trading3GenericInstances {
    def apply[M[_]](implicit mo: Trading3Generic[M]): Trading3Generic[M] = mo

    implicit object Trading3GenericList extends Trading3Generic[List] {
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

    implicit object Trading2GenericOption extends Trading3Generic[Option] {
      override def clientOrder(): Kleisli[Option, ClientOrder, Order] = Kleisli[Option, ClientOrder, Order] {
        (client: ClientOrder) =>
          Option(Order(client.name + " clientOrde"))
      }

      override def execute(m: Market, a: Account): Kleisli[Option, Order, Execution] =
        Kleisli[Option, Order, Execution] { (order: Order) =>
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
  }

  trait Trading3GenericInstancesSyntax {
    object syntax {

      import cats.data.Kleisli
//      import cats.FlatMap

      def clientOrder[M[_]](implicit trading: Trading3Generic[M]): Kleisli[M, ClientOrder, Order] =
        trading.clientOrder()
      def execute[M[_]](m: Market, a: Account)(implicit trading: Trading3Generic[M]): Kleisli[M, Order, Execution] =
        trading.execute(m, a)
      def allocate[M[_]](as: List[Account])(implicit trading: Trading3Generic[M]): Kleisli[M, Execution, Trade] =
        trading.allocate(as)

    }
  }

  def ejemplo1BasicList(): Unit = {
    println(s"-*- ejemplo1BasicList() -*-")
    val elem =
      Trading3Generic[List].clientOrder() andThen Trading3Generic[List].execute(Market("Market"), Account("Account"))
    println(s"-->${elem.run(ClientOrder("ClienteOrderPrueba"))}")

    val elem2 = Trading3Generic[Option]
      .clientOrder() andThen Trading3Generic[Option].execute(Market("Market"), Account("Account"))
    println(s"-->${elem2.run(ClientOrder("ClienteOrderPrueba"))}")
    println()
  }

  // Habría que pensar en definir un componente con las operaciones de negocio.
  def businessService1(): Kleisli[List, ClientOrder, Execution] = {
    import Trading3Generic.syntax._
    clientOrder[List] andThen execute[List](Market("Market"), Account("Account"))
  }

  def ejemplo2Syntax(): Unit = {
    import Trading3Generic.syntax._

    println(s"-*- ejemplo2Syntax() -*-")
    val elem = clientOrder[List] andThen execute[List](Market("Market"), Account("Account"))
    println(s"-->${elem.run(ClientOrder("ClienteOrderPrueba"))}")

    val elem2 = clientOrder[Option] andThen execute[Option](Market("Market"), Account("Account"))
    println(s"-->${elem2.run(ClientOrder("ClienteOrderPrueba"))}")
    println()
  }

  ejemplo1BasicList()
  ejemplo2Syntax()

}
