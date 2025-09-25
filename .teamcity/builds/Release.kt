package builds

import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.ParameterDisplay
import jetbrains.buildServer.configs.kotlin.toId

class Release(id: String, name: String) :
    BuildType(
        {
          this.id(id.toId())
          this.name = name

          params {
            text(
                "releaseVersion",
                "",
                label = "Version to release",
                display = ParameterDisplay.PROMPT,
                allowEmpty = false,
            )
            text(
                "nextSnapshotVersion",
                "",
                label = "Next snapshot version",
                description = "Next snapshot version to set after release",
                display = ParameterDisplay.PROMPT,
                allowEmpty = false,
            )

          }

          steps {
            setVersion("Set release version", "%releaseVersion%")
            mavenPackage("package")
            sha256("sha256 sum")

            commitAndPush(
                "Push release version",
                "build: release version %releaseVersion%",
            )

            setVersion("Set next snapshot version", "%nextSnapshotVersion%")

            commitAndPush(
                "Push next snapshot version",
                "build: update version to %nextSnapshotVersion%",
            )
          }

          artifactRules =
                """
                +:target/neo4j-aws-glue*jar
                +:target/neo4j-aws-glue*.sha256
                """
                    .trimIndent()

          requirements { runOnLinux(LinuxSize.SMALL) }
        },
    )
