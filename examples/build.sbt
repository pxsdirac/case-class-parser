lazy val core =
  CommonProject(id = "examples", base = ".")
    .dependsOn(Subprojects.typesafeConfig)
