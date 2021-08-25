package es.ams.modulelayer

object ModuleLayerExample4Model {
  
    // Extract
    sealed trait ExtractDataResult{
        val id: Int
        val name: String
        val result: Boolean
    }
    case class OkExtract(
        val id: Int, 
        val name: String, 
        val result: Boolean) extends ExtractDataResult

    // Transform
    sealed trait TransformedResult{
        val id: Int
        val name: String
        val result: Boolean        
    }
    case class OkTransformed(
        val id: Int, 
        val name: String, 
        val result: Boolean) extends TransformedResult

    // Loader
    sealed trait LoaderResult{
       val id: Int
       val name: String
       val result: Boolean        
    } 
    case class OkLoader(
        val id: Int, 
        val name: String, 
        val result: Boolean) extends LoaderResult

}
