package es.ams.macrosamples

import es.ams.macrosamples.domain.Rectangle
import es.ams.basic.OperationsMacro
import io.getquill._
import io.getquill.context.Context

import scala.language.experimental.macros

trait Queries {

  this: Context[_, _] =>

  def insertMacro[T](entity: T): Quoted[Insert[T]] = macro OperationsMacro.insert[T]

  def insertAutoGenerated[T](entity: T): Quoted[ActionReturning[T, Int]] = macro OperationsMacro.insertAutoGenerated[T]

  def update[T](entity: T): Quoted[Update[T]] = macro OperationsMacro.update[T]

  def delete[T](entity: T): Quoted[Delete[T]] = macro OperationsMacro.delete[T]

  def selectAll() = {
    quote {
      query[Rectangle]
    }
  }

}