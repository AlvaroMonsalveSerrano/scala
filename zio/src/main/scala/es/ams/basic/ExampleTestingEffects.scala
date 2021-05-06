package es.ams.basic

import zio.{RIO, Task}

object ExampleTestingEffects {

  final case class Config(server: String, port: Int)

  trait DatabaseOps {
    def getTableNames: Task[List[String]]
    def getColumnNames(table: String): Task[List[String]]
  }
  object DatabaseOps extends DatabaseOps {
    override def getTableNames: Task[List[String]] = Task.succeed(List("Table1", "Table2"))

    override def getColumnNames(table: String): Task[List[String]] = table match {
      case "Table1" => Task.succeed(List("Colum1Table1", "Colum2Table1", "Colum3Table1"))
      case "Table2" => Task.succeed(List("Colum1Table2", "Colum2Table2", "Colum3Table2"))
      case _        => Task.succeed(List(""))
    }
  }

  type UserID      = Int
  type UserProfile = String

  object Database {
    trait Service {
      def lookup(id: UserID): Task[UserProfile]
      def update(id: UserID, profile: UserProfile): Task[Unit]
    }
  }

  // Definición del módulo Database.
  trait Database {
    def database: Database.Service
  }

  // Define el componente que simula la base de datos.
  trait DatabaseLive extends Database {
    def database: Database.Service = new Database.Service {
      override def lookup(id: UserID): Task[UserProfile] =
        RIO.succeed("UserProfileTest")

      override def update(id: UserID, profile: UserProfile): Task[Unit] =
        RIO.succeed(println(s"Updated something..."))
    }
  }
  object DatabaseLive extends DatabaseLive

  object db {
    def lookup(id: UserID): RIO[Database, UserProfile] =
      RIO.accessM(_.database.lookup(id))

    def update(id: UserID, profile: UserProfile): RIO[Database, Unit] =
      RIO.accessM(_.database.update(id, profile))
  }

}
