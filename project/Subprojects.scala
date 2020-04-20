// format: off
import sbt._

object Subprojects {
  lazy val core = Project(id = "core", base = file("core"))
  lazy val typesafeConfig = Project(id = "typesafe-config", base = file("typesafe-config"))
  lazy val examples = Project(id = "examples", base = file("examples"))
}
