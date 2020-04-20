package com.github.pxsdirac.ccp.core.parser

import scala.util.Try

/**
  * a parser to extract A from data with key
  * @tparam D
  * @tparam A
  */
trait KeyValueParser[D, A] {
  def parseValueByKey(data: D, key: String): Try[A]
}
