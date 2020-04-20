package typesafe.config

import com.github.pxsdirac.ccp.core.parser.KeyValueParser
import com.github.pxsdirac.ccp.typesafe.config.Implicits
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration._
import scala.util.Try

object SelfDefinedParserExample extends App {
  val config = ConfigFactory.parseString("""
      |{
      |  subSettings1 {
      |    p1 = 1
      |    p2 = 1
      |  }
      |
      |  subSettings2 = [
      |    {
      |      p2 = 2
      |    },
      |    {
      |      p1 = "hello"
      |      p2 = 1
      |      p3 = 2.0
      |    }
      |  ]
      |}
      |""".stripMargin)
  case class Settings(subSettings1: SubSettings1, subSettings2: List[SubSettings2])
  case class SubSettings1(p1: Option[Int], p2: FiniteDuration, p3: Boolean = true)
  case class SubSettings2(p1: Option[String], p2: Int, p3: Double = 1.0)

  object HighPriority extends Implicits {
    implicit val finiteDurationKeyValueParser: KeyValueParser[Config, FiniteDuration] =
      new KeyValueParser[Config, FiniteDuration] {
        override def parseValueByKey(data: Config, key: String): Try[FiniteDuration] = Try {
          data.getInt(key).seconds
        }
      }
  }
  import com.github.pxsdirac.ccp.core._
  import HighPriority._

  val settings = parse[Settings](config)
  println(settings)
}
