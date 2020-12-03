package es.ams

trait Template {
  lazy val greeting: String = "hello"
}

object Application extends Template with App {
  println(greeting)
}
