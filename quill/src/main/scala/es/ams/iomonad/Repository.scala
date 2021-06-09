package es.ams.iomonad

import scala.concurrent.Future

object Repository {

  import es.ams.macrosamples.domain._

  trait BaseRepository[F[_], E] {
    def insert(entity: E): F[E]
    def update(entity: E): F[E]
    def delete(entity: E): F[E]
  }

  trait IRectangle extends BaseRepository[Future, Rectangle] {
    def findByID(id: Int): Future[Rectangle]
  }

}
