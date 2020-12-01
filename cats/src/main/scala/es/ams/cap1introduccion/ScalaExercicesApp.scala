package es.ams.cap1introduccion

object ScalaExercicesApp extends App{

  import cats.Semigroup
  import cats.implicits._

  val aMap = Map("foo" `->` Map("bar" `->` 5))
  val anotherMap = Map("foo" `->`Map("bar" `->` 6))
  val combinedMap = Semigroup[Map[String, Map[String, Int]]].combine(aMap, anotherMap)

  println(s"combinedMap.get('foo')=${combinedMap.get("foo")} ")


}
