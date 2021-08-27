package es.ams.modulelayer

object ModuleLayerExample4Exception {

  sealed trait ExtractorException  extends Exception
  case class BasicErrorException() extends ExtractorException

  sealed trait TransformedException      extends Exception
  case class BasicTransformedException() extends TransformedException

  sealed trait LoaderException      extends Exception
  case class BasicLoaderException() extends LoaderException
  case class ErrorLoaderException() extends LoaderException

}
