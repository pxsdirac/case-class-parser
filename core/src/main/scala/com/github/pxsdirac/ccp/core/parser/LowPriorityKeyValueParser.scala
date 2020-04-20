package com.github.pxsdirac.ccp.core.parser

import shapeless.Lazy

import scala.util.Try

/**
  * provide low priority type class derivation for [[KeyValueParser]]
  */
trait LowPriorityKeyValueParser {
  private def fromFunction[D, A](f: (D, String) => Try[A]): KeyValueParser[D, A] = new KeyValueParser[D, A] {
    override def parseValueByKey(data: D, key: String): Try[A] = f(data, key)
  }

  implicit def productKeyValueParser[D, A](
    implicit selfKeyValueParser: KeyValueParser[D, D],
    aProductParser: Lazy[CaseClassParser[D, A]]
  ): KeyValueParser[D, A] = fromFunction[D, A] {
    case (data, key) =>
      val selfTry = selfKeyValueParser.parseValueByKey(data, key)
      selfTry flatMap aProductParser.value.parse
  }

  implicit def optionKeyValueParser[D, A](implicit parser: KeyValueParser[D, A]): KeyValueParser[D, Option[A]] =
    fromFunction[D, Option[A]] {
      case (data, key) =>
        parser
          .parseValueByKey(data, key)
          .map(Option.apply)
          .recover {
            case _ => None
          }
    }
}
