package es.ams.modulelayer

import scala.concurrent._
import es.ams.modulelayer.ModuleLayerExample4Model._

object ModuleLayerExemple4Common {

  def loaderData(data: OkTransformed)(implicit ec: ExecutionContext): Future[LoaderResult] = {
    Future { throw new Exception("Booom!!"); OkLoader(id = data.id, name = data.name, result = true) }
  }

}
