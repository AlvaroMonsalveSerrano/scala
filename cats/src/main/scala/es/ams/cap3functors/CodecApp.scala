package es.ams.cap3functors

object CodecApp extends App {

  import Codec.syntax._

  val cadenaJson = codifica[String]("cadena")
  println(s" codifica('cadena')=${cadenaJson} ")
  println()

  val cadenaString1 = decodifica[String](cadenaJson)
  println(s" codifica('{cadena}')=${cadenaString1} ")
  println()


  val ejem1 = codifica[Double](123.4)
  println(s" ejem1=${ejem1} ")
  println()


  val ejem2 = decodifica[Double]("123.4")
  println(s" ejem2=${ejem2} ")
  println()


  // Pendiente las pruebas d IMAP
//  println( "encode('cadena')=" + Codec.apply[String].imap( (elem:String) => "{" + elem + "}"   ,
  //  (elem:String) => elem.substring(0, elem.length)  ).encode("cadena") )
//  println( "decode('{cadena}')=" + Codec.apply[String].imap( (elem:String) => "{" + elem + "}"   ,
  //  (elem:String) => elem.substring(0, elem.length)  ).decode("{cadena}") )

//  println( "decode('{cadena}')=" + Codec.apply[String].imap(  (elem:String) => elem.substring(0, elem.length)  ,
  //  (elem:String) => "{" + elem + "}"   ).decode("{cadena}") )
//  println( "decode('cadena')=" +   Codec.apply[String].imap(  (elem:String) => elem.substring(0, elem.length)  ,
  //  (elem:String) => elem    ).encode("cadena" ) )

}
