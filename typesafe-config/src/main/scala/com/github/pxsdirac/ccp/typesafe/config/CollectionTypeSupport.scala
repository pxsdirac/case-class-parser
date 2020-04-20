package com.github.pxsdirac.ccp.typesafe.config

import com.github.pxsdirac.ccp.core.parser.{CaseClassParser, KeyValueParser}
import com.typesafe.config.Config
import shapeless.Lazy

import scala.collection.JavaConverters._
import scala.util.Try

object CollectionTypeSupport {
  trait LowPriority {
    implicit def configToAnyList[A](
      implicit caseClassParser: Lazy[CaseClassParser[Config, A]]
    ): KeyValueParser[Config, List[A]] = (data: Config, key: String) => {
      val listOfTryA = data.getConfigList(key).asScala.toList.map { config =>
        caseClassParser.value.parse(config)
      }
      listOfTryA
        .foldLeft(Try(List.empty[A])) {
          case (aggTry: Try[List[A]], itemTry: Try[A]) =>
            for {
              agg <- aggTry
              item <- itemTry
            } yield {
              item :: agg
            }
        }
        .map(_.reverse)
    }

    implicit def configToAnySet[A](
      implicit listKeyValueParser: KeyValueParser[Config, List[A]]
    ): KeyValueParser[Config, Set[A]] = (config, key) => {
      listKeyValueParser.parseValueByKey(config, key).map(_.toSet)
    }
  }
}
trait CollectionTypeSupport extends CollectionTypeSupport.LowPriority {
  implicit val configToIntList: ConfigTo[List[Int]] = fromFunction {
    case (config, key) => config.getIntList(key).asScala.toList.map(_.intValue())
  }

  implicit val configTiLongList: ConfigTo[List[Long]] = fromFunction {
    case (config, key) => config.getLongList(key).asScala.toList.map(_.longValue())
  }

  implicit val configToStringList: ConfigTo[List[String]] = fromFunction {
    case (config, key) => config.getStringList(key).asScala.toList
  }

  implicit val configToDoubleList: ConfigTo[List[Double]] = fromFunction {
    case (config, key) => config.getDoubleList(key).asScala.toList.map(_.doubleValue())
  }

  implicit val configTiFloatList: ConfigTo[List[Float]] = fromFunction {
    case (config, key) => config.getDoubleList(key).asScala.toList.map(_.floatValue())
  }

  implicit val configToBooleanList: ConfigTo[List[Boolean]] = fromFunction {
    case (config, key) => config.getBooleanList(key).asScala.toList.map(_.booleanValue())
  }

  implicit val configToConfig: ConfigTo[Config] = fromFunction {
    case (config, key) => config.getConfig(key)
  }

}
