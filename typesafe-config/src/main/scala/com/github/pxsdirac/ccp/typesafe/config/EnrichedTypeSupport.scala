package com.github.pxsdirac.ccp.typesafe.config

import scala.concurrent.duration.{Duration, FiniteDuration}

trait EnrichedTypeSupport {
  implicit val configToJDuration: ConfigTo[java.time.Duration] = fromFunction {
    case (config, key) => config.getDuration(key)
  }

  implicit val configToSDuration: ConfigTo[FiniteDuration] = fromFunction {
    case (config, key) =>
      val jDuration = config.getDuration(key)
      Duration.fromNanos(jDuration.toNanos)
  }
}
