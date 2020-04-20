package com.github.pxsdirac.ccp.core.parser

import scala.util.Try

/**
  * parser to parse data with type D to the target type A
  * @tparam D data source type, D should be a kv like type so that we can extract a value by key.
  * @tparam A target type
  */
trait CaseClassParser[D, A] {
  def parse(data: D): Try[A]
}
