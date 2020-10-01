package es.ams.cap3functors

// Definición de un functor bidireccional (= Invariant Functor)


trait Codec[A] {
  def encode(elem:A):String
  def decode(elem:String): A

  def imap[B]( deco: A => B, enc: B => A ): Codec[B] = {
    val self = this
    new Codec[B] {
      override def encode(elem: B): String = self.encode(enc(elem))

      override def decode(elem: String): B = deco(self.decode(elem))
    }
  }
}

object Codec extends  CodecInstances with CodecSyntax

trait CodecInstances{
  def apply[A](implicit C:Codec[A]): Codec[A] = C

  implicit val jsonCodec = new Codec[String]{

    override def encode(elem: String): String = "{" + elem + "}"

    override def decode(elem: String): String = elem.substring(1, elem.length-1)
  }

  implicit val doubleCodec = new Codec[Double]{

    override def encode(elem: Double): String = elem.toString

    override def decode(elem: String): Double = elem.toDouble
  }


}

trait CodecSyntax{
  object syntax{
    def codifica[A](elem:A)(implicit C:Codec[A]):String = C.encode(elem)

    def decodifica[A](elem:String)(implicit C:Codec[A]):A = C.decode(elem)
  }
}
