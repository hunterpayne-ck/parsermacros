package org.duhemm.parsermacro

import scala.reflect.api.Universe

trait UniverseUtils {
  val universe: Universe

  import universe._

  /**
   * Computes the correct name for the object that holds the implementation we're looking for.
   */
  def erasedName(sym: Symbol): String = {
    def rootOrEmpty(sym: Symbol) = {
      val rootSym = rootMirror.RootClass
      val emptyPackageClassSym = rootMirror.EmptyPackageClass
      sym == rootSym || sym == emptyPackageClassSym
    }

    def ownersOf(sym: Symbol): Stream[Symbol] = sym.owner #:: ownersOf(sym.owner)

    ownersOf(sym).takeWhile(!rootOrEmpty(_)).toList.reverse.partition(_.isPackage) match {
      case (Nil, objs)  => objs.map(_.name).mkString("", "$", "$")
      case (pkgs, objs) => pkgs.map(_.name).mkString(".") + (objs.map(_.name).mkString(".", "$", "$"))
    }
  }

}
