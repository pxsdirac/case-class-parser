package com.github.pxsdirac.ccp.core.parser

import shapeless.LabelledGeneric

/**
  * some time, the key in data source make be different from the label name in case class.
  * you can provide a nameMapping to have a conversion between them
  * @tparam A no meaning for this trait, but it is important to provide different nameMapping for different class
  */
trait NameMapping[A] {

  /**
    * get the mapped name from origin name
    * @param name origin name, this should be the name in the case class
    * @return mapped name, this should be the name of key in data source
    */
  def get(name: String): String
}

object NameMapping {

  def fromPF[A] = new {
    def apply[R](pf: PartialFunction[String, String])(implicit generic: LabelledGeneric.Aux[A, R]): NameMapping[R] = {
      (name: String) =>
        pf.applyOrElse[String, String](name, _ => name)
    }
  }

  def empty[A]: NameMapping[A] = (name: String) => name

  def fromNameMapping[A](nameMapping: NameMapping[_]): NameMapping[A] = (name: String) => nameMapping.get(name)
}
