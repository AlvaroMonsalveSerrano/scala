package es.ams.postgresql

import com.dimafeng.testcontainers.{ForAllTestContainer, PostgreSQLContainer}
import org.scalatest.flatspec.AnyFlatSpec

import java.sql.DriverManager

class PostgresqlEjem1 extends AnyFlatSpec with ForAllTestContainer {

  override val container: PostgreSQLContainer = {
    val psql: PostgreSQLContainer = new PostgreSQLContainer()
    psql.start()
    psql
  }

  def stop() = {
    container.stop()
  }

//  val config: Config = { //
//    val components = List(
//      container.getJdbcUrl,
//      s"user=${container.getUsername}",
//      s"password=${container.getPassword}"
//    )
//    ConfigFactory
//      .empty()
//      .withValue(
//        "url",
//        ConfigValueFactory.fromAnyRef(components.mkString("&"))
//      )
//  }

  "PostgreSQL container" should "be started" in {
    Class.forName(container.driverClassName)
    val connection = DriverManager.getConnection(container.jdbcUrl, container.username, container.password)

    assert(connection != null)

  }

}
