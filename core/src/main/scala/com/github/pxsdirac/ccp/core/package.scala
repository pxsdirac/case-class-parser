package com.github.pxsdirac.ccp

import scala.util.Try

package object core {

  /**
    * the core api for the parser
    * @tparam A
    * @return
    */
  def parse[A] = new {
    import parser.CaseClassParser
    def apply[D](data: D)(implicit caseClassParser: CaseClassParser[D, A]): Try[A] = caseClassParser.parse(data)
  }
}
