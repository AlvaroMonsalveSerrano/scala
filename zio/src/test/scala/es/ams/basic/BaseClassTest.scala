package es.ams.basic

import org.scalatest.flatspec.AnyFlatSpec

import java.net.URI

class BaseClassTest extends AnyFlatSpec {

  val nameFile: String = "file1test.data"

  def getURIFileTest(nameFile: String): URI = this.getClass.getClassLoader.getResource(nameFile).toURI

}
