package es.ams.basic

import es.ams.basic.ExampleBasicOperations.putStrLn
import es.ams.basic.ExampleTestingEffects.{Config, Database, DatabaseOps, DatabaseLive, UserID, UserProfile, db}
import zio.{RIO, Runtime, Task, URIO, ZIO}

class TestingEffectsTest extends BaseClassTest {

  "ZIO Testing Effects" should "Environments" in {

    val environmentsTest: ZIO[Int, Nothing, Int] = for {
      env <- ZIO.environment[Int]
      _   <- putStrLn(s"The value os the environment is: ${env}")
    } yield (env)

    val resultOperation: Int = Runtime.default.unsafeRun(environmentsTest.provide(20))
    println(s"Result=${resultOperation}")
    assertResult(20)(resultOperation)

    val configString: URIO[Config, String] = for {
      server <- ZIO.access[Config](_.server)
      port   <- ZIO.access[Config](_.port)
    } yield (s"Server=$server, port:$port")

    val config               = Config(server = "serverTest", port = 8888)
    val resultOperationClass = Runtime.default.unsafeRun(configString.provide(config))
    assertResult("Server=serverTest, port:8888")(resultOperationClass)

    val columnsTable1: ZIO[DatabaseOps, Throwable, List[String]] = for {
      tables  <- ZIO.accessM[DatabaseOps](_.getTableNames)
      columns <- ZIO.accessM[DatabaseOps](_.getColumnNames("Table1"))
    } yield (columns)
    val resultOperationDatabase1 = Runtime.default.unsafeRun(columnsTable1.provide(DatabaseOps))
    assertResult(List("Colum1Table1", "Colum2Table1", "Colum3Table1"))(resultOperationDatabase1)

    val columnsTable2: ZIO[DatabaseOps, Throwable, (List[String], List[String])] = for {
      tables  <- ZIO.accessM[DatabaseOps](_.getTableNames)
      columns <- ZIO.accessM[DatabaseOps](_.getColumnNames("Table1"))
    } yield (tables, columns)
    val resultOperationDatabase2 = Runtime.default.unsafeRun(columnsTable2.provide(DatabaseOps))
    assertResult((List("Table1", "Table2"), List("Colum1Table1", "Colum2Table1", "Colum3Table1")))(
      resultOperationDatabase2
    )

  }

  it should "Environmental Effects" in {

    val userId: UserID = 10

    val envEffects: RIO[Database, UserProfile] = for {
      profile <- db.lookup(userId)
    } yield (profile)

    val mainEnvEffects: Task[UserProfile] = envEffects.provide(DatabaseLive)

    val result: UserProfile = Runtime.default.unsafeRun(mainEnvEffects)
    assertResult("UserProfileTest")(result)

  }

}
