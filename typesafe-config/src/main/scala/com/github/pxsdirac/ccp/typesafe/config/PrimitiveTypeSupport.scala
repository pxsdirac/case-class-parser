package com.github.pxsdirac.ccp.typesafe.config

import com.typesafe.config.Config

trait PrimitiveTypeSupport {
  implicit val configToInt: ConfigTo[Int] = fromFunction {
    case (config, key) => config.getInt(key)
  }

  implicit val configToLong: ConfigTo[Long] = fromFunction {
    case (config, key) => config.getLong(key)
  }

  implicit val configToString: ConfigTo[String] = fromFunction {
    case (config, key) => config.getString(key)
  }

  implicit val configToDouble: ConfigTo[Double] = fromFunction {
    case (config, key) => config.getDouble(key)
  }

  implicit val configToFloat: ConfigTo[Float] = fromFunction {
    case (config, key) => config.getDouble(key).toFloat
  }

  implicit val configToBoolean: ConfigTo[Boolean] = fromFunction {
    case (config, key) => config.getBoolean(key)
  }

}
