# case-class-parser
a type safe library to parse some kv-like data to scala case class

# supported data source and import
> note: this repo did not upload to maven central yet, so you should clone it and publish by your self.
```
$ git clone git@github.com:pxsdirac/case-class-parser.git
$ cd case-class-parser
$ sbt publishLocal
```  
here are the supported data source:
* [Typesafe Config](https://github.com/lightbend/config)
```
libraryDependencies += "com.github.pxsdirac" %% "case-class-parser-typesafe-config" % "0.1.0-SNAPSHOT"
```
# supported features
* default field value support
> parsed case class will use its default value for some fields if it is not provided. 
* name mapping between filed name of case class and key in the data source
> the filed name in the case class maybe different from the key in the data source, you can provide a name mapping to add your own role for that.
* self defined parser rule
> sometimes, the default behaviors is not good enough for your requirement. you can implement your own parser for some target type.

# usage
### basic usage
[demo for this section](examples/src/main/scala/typesafe/config/BasicExample.scala)

the config file `application.conf`
```hocon
{
  subSettings1 {
    p1 = 1
    p2 = 1 seconds
  }

  subSettings2 = [
    {
      p2 = 2
    },
    {
      p1 = "hello"
      p2 = 1
      p3 = 2.0
    }
  ]
}
```
parse config file to settings
```scala
  import com.typesafe.config.ConfigFactory
  import scala.concurrent.duration.FiniteDuration

  case class Settings(subSettings1: SubSettings1,
                      subSettings2: List[SubSettings2])
  case class SubSettings1(p1: Option[Int],
                          p2: FiniteDuration,
                          p3: Boolean = true)
  case class SubSettings2(p1: Option[String], p2: Int, p3: Double = 1.0)

  val config = ConfigFactory.defaultApplication()

  import com.github.pxsdirac.ccp.core._
  import com.github.pxsdirac.ccp.typesafe.config._
  val settings = parse[Settings](config)
  println(settings)
```
the result of settings is 
```
Success(Settings(SubSettings1(Some(1),1 second,true),List(SubSettings2(None,2,1.0), SubSettings2(Some(hello),1,2.0))))
```

### with name mapping
[demo for this section](examples/src/main/scala/typesafe/config/NameMappingExample.scala)

the key in config file should be the field name of case class with default behavior, but you can use your own key by name mapping.
config file:
```hocon
{
  subSettings1 {
    p1MappedKey = 1
    p2 = 1 seconds
  }

  subSettings2 = [
    {
      p2 = 2
    },
    {
      p1 = "hello"
      p2 = 1
      p3 = 2.0
    }
  ]
}
```
```scala
import com.github.pxsdirac.ccp.core.parser.NameMapping

  implicit val subSettings1NameMapping = NameMapping.fromPF[SubSettings1] {
    case "p1" => "p1MappedKey"
  }

  val config = ConfigFactory.defaultApplication()

  import com.github.pxsdirac.ccp.core._
  import com.github.pxsdirac.ccp.typesafe.config._
  val settings = parse[Settings](config)
  println(settings)
```
### self defined parse rule
[demo for this section](examples/src/main/scala/typesafe/config/SelfDefinedParserExample.scala)

the parse rule is auto derived, but you can define your own parse rule by adding implicit parser with higher priority.

config file:
```hocon
{
  subSettings1 {
    p1 = 1
    p2 = 1
  }

  subSettings2 = [
    {
      p2 = 2
    },
    {
      p1 = "hello"
      p2 = 1
      p3 = 2.0
    }
  ]
}
```  
```scala
import com.github.pxsdirac.ccp.core.parser.KeyValueParser
  import com.github.pxsdirac.ccp.typesafe.config.Implicits
  import com.typesafe.config.Config

  import scala.concurrent.duration._
  import scala.util.Try

  object HighPriority extends Implicits {
    implicit val finiteDurationKeyValueParser
      : KeyValueParser[Config, FiniteDuration] =
      new KeyValueParser[Config, FiniteDuration] {
        override def parseValueByKey(data: Config,
                                     key: String): Try[FiniteDuration] = Try {
          data.getInt(key).seconds
        }
      }
  }

  import com.github.pxsdirac.ccp.core._
  import HighPriority._
  val config = ConfigFactory.defaultApplication()

  val settings = parse[Settings](config)
  println(settings)
```
