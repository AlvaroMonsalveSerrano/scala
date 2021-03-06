package es.ams.iomonad

import es.ams.basic.OperationsMacro
import io.getquill._
import io.getquill.context.Context
import io.getquill.monad.IOMonad

import scala.language.experimental.macros

import es.ams.macrosamples.domain.Rectangle

object RepositoryCommon {

  trait RectangleContext extends IOMonad {
    this: Context[_, _] =>

    def insert[T](entity: T): Quoted[Insert[T]] = macro OperationsMacro.insert[T]

    def insertAutoGenerated[T](entity: T): Quoted[ActionReturning[T, Int]] =
      macro OperationsMacro.insertAutoGenerated[T]

    def updateR[T](entity: T): Quoted[Update[T]] = macro OperationsMacro.update[T]

    def deleteR[T](entity: T): Quoted[Delete[T]] = macro OperationsMacro.delete[T]

    def findById(id: Int) = quote {
      query[Rectangle]
        .filter(rec => rec.id_rec == lift(id))
    }

  }

  abstract class AbstractRepository(configPrefix: String) {
    val ctx = new PostgresAsyncContext(SnakeCase, configPrefix) with RectangleContext
  }

}
