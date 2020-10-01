package es.ams.cap4monadas

/**
  * La mónada Reader es una herramienta para realizar inyección de dependencias.
  *
  * Otros usos de la mónada Reader son los siguientes:
  *
  * - Construir una programa batch que puede ser fácilmente representado por una  función.
  * - Cuando necesitamos diferir la inyección de un parámetro o conjunto de parámetros.
  * - Cuando queremos realiza un test de una parte de un programa de forma aislada. Para probar funciones puras.
  *
  */
object Ejem2ReaderMonad extends App {

  import cats.data.Reader

  type DbReader[A] = Reader[Db, A] // Definición del alias

  def findUsername(userId: Int): DbReader[Option[String]] = {
    Reader(bbdd => bbdd.usernames.get(userId))
  }

  def checkPassword(username:String, password:String): DbReader[Boolean] = {
    Reader(bbdd => bbdd.passwords.get(username).contains(password)  )
  }


  def checkLogin(userId:Int, password:String): DbReader[Boolean] = {
    val result = for {
      user <- findUsername(userId)
      valid <- checkPassword(user.get, password)
    }yield{
      valid
    }
    result

  }

  // Definición de la configuración de una BBDD
  case class Db(usernames: Map[Int, String], passwords: Map[String, String])

  val user = Map(1 -> "user1", 2 -> "user2")
  val pswd = Map( "user1" -> "pwd1", "user2" -> "pwd2")
  val BBDD = Db(user, pswd)

  println( s" CheckLogin (id=1, pwd=pwd1)OK=${checkLogin(1,"pwd1").run(BBDD)}" )
  println( s" CheckLogin (id=1, pwd=pwd)KO=${checkLogin(1,"pwd").run(BBDD)}" )
  println()

}
