package es.ams.dependencyinyector

case class Account private[dependencyinyector] (name: String)
case class Market private[dependencyinyector] (name: String)
case class Order private[dependencyinyector] (name: String)
case class ClientOrder private[dependencyinyector] (name: String)
case class Execution private[dependencyinyector] (name: String)
case class Trade private[dependencyinyector] (name: String)

private[dependencyinyector] object TypeEjem1Kleisli {
  type TAccount     = Account
  type TMarket      = Market
  type TOrder       = Order
  type TClientOrder = ClientOrder
  type TExecution   = Execution
  type TTrade       = Trade
}
