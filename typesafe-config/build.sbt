val config = "com.typesafe" % "config" % "1.4.0"

lazy val core =
  CommonProject(id = "typesafe-config", base = ".")
    .settings(libraryDependencies := Seq(config), name := "case-class-parser-typesafe-config", version := "0.1.0")
    .dependsOn(Subprojects.core)
