package es.ams.modulelayer

import zio._

import es.ams.modulelayer.ModuleLayerExample4Exception._
import es.ams.modulelayer.ModuleLayerExample4Model._

object ModuleLayerExample4Module {

    type Extractor = Has[Extractor.Service]
    object Extractor{
        trait Service{
            def extractData(): IO[ExtractorException, ExtractDataResult]
        }

        case class ExtractorImpl() extends Extractor.Service{
            override def extractData(): IO[ExtractorException,ExtractDataResult] = 
                ZIO.succeed( OkExtract(id=1, name="Test1", result=true) )
        }

        val live: ZLayer[Any, Nothing, Extractor] = ZLayer.succeed(ExtractorImpl())
    }

    type Transformer = Has[Transformer.Service]
    object Transformer{
        trait Service{
            def doTransformer(data: ExtractDataResult): IO[TransformedException, TransformedResult]
        }

        case class TransformerImpl() extends Transformer.Service {
            override def doTransformer(data: ExtractDataResult): IO[TransformedException,TransformedResult] = 
                data match {
                    case dataIn: OkExtract => ZIO.succeed(OkTransformed(id=dataIn.id, name=dataIn.name, result=true))
                    case _                 => ZIO.fail(BasicTransformedException())
                }
        }

        val live: ZLayer[Any, Nothing, Transformer] = ZLayer.succeed(TransformerImpl())
    }
  

    type Loader = Has[Loader.Service]
    object Loader {
        trait Service {
            def doLoader(data: TransformedResult): IO[LoaderException, LoaderResult]
        }

        case class LoaderImpl() extends Loader.Service {
            override def doLoader(data: TransformedResult): IO[LoaderException,LoaderResult] = 
                data match {
                    case dataIn: OkTransformed => ZIO.succeed(OkLoader(id= dataIn.id, name=dataIn.name, result=true))
                    case _                     => ZIO.fail(BasicLoaderException())
                }
        }

        val live: ZLayer[Any, Nothing, Loader] = ZLayer.succeed(LoaderImpl())
    }
    
}

import ModuleLayerExample4Module.Extractor
package object extractor {
    def extractData = ZIO.accessM[Extractor](_.get.extractData())
}

import ModuleLayerExample4Module.Transformer
package object transformer {
    def transformer(data: ExtractDataResult) = ZIO.accessM[Transformer](_.get.doTransformer(data))
}

import ModuleLayerExample4Module.Loader
package object loader {
    def loader(data: TransformedResult) = ZIO.accessM[Loader](_.get.doLoader(data))
}
