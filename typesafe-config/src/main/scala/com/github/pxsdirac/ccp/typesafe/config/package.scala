package com.github.pxsdirac.ccp.typesafe

import com.github.pxsdirac.ccp.core.parser.KeyValueParser
import com.typesafe.config.Config

import scala.util.Try

package object config extends Implicits {
  type ConfigTo[A] = KeyValueParser[Config, A]

  private[config] def fromFunction[A](f: (Config, String) => A): ConfigTo[A] =
    (data: Config, key: String) =>
      Try {
        f(data, key)
      }
}
