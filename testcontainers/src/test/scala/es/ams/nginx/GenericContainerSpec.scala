package es.ams.nginx

import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}
import org.scalatest.flatspec.AnyFlatSpec
import org.testcontainers.containers.wait.strategy.Wait

import java.net.URL
import scala.io.Source

/** https://github.com/testcontainers/testcontainers-scala
  */
class GenericContainerSpec extends AnyFlatSpec with ForAllTestContainer {

  override def container: GenericContainer = {
    val nginx = GenericContainer(
      "nginx:latest",
      exposedPorts = Seq(80),
      waitStrategy = Wait.forHttp("/")
    )
    nginx.start()
    nginx
  }

  "GenericContainer" should "start nginx and expose 80 port" in {
    assert(
      Source
        .fromInputStream(
          new URL(s"http://${container.containerIpAddress}:${container.mappedPort(80)}/")
            .openConnection()
            .getInputStream
        )
        .mkString
        .contains("If you see this page, the nginx web server is successfully installed")
    )
  }

}
