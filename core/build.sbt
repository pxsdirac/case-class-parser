val shapeless = "com.chuusai" %% "shapeless" % "2.3.3"

lazy val core =
  CommonProject(id = "core", base = ".")
    .settings(libraryDependencies := Seq(shapeless), name := "case-class-parser-core", version := "0.1.0")
