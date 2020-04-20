package typesafe.config

import com.github.pxsdirac.ccp.core.parser.NameMapping
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

object NameMappingExample extends App {
  val config = ConfigFactory.parseString("""
      |{
      |  subSettings1 {
      |    p1MappedKey = 1
      |    p2 = 1 seconds
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
      |
      |""".stripMargin)

  case class Settings(subSettings1: SubSettings1, subSettings2: List[SubSettings2])
  case class SubSettings1(p1: Option[Int], p2: FiniteDuration, p3: Boolean = true)
  case class SubSettings2(p1: Option[String], p2: Int, p3: Double = 1.0)

  import com.github.pxsdirac.ccp.core._
  import com.github.pxsdirac.ccp.typesafe.config._
  implicit val subSettings1NameMapping = NameMapping.fromPF[SubSettings1] {
    case "p1" => "p1MappedKey"
  }

  val settings = parse[Settings](config)
  println(settings)
}
