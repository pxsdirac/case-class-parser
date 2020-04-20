import sbt.Keys.{organization, scalaVersion}
import sbt.{Project, file}

object CommonProject {
  def apply(id: String, base: String) =
    Project(id = id, base = file(base))
      .settings(scalaVersion := "2.12.9", organization := "com.github.pxsdirac")
}
