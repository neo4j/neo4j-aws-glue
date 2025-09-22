package builds

import jetbrains.buildServer.configs.kotlin.AbsoluteId
import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.ParameterDisplay
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.toId

private const val DRY_RUN = "dry-run"

class Release(id: String, name: String) :
    BuildType(
        {
          this.id(id.toId())
          this.name = name

          templates(AbsoluteId("FetchSigningKey"))

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

            checkbox(
                DRY_RUN,
                "true",
                "Dry run?",
                description =
                    "Whether to perform a dry run where nothing is published and released",
                display = ParameterDisplay.PROMPT,
                checked = "true",
                unchecked = "false",
            )

          }

          steps {
            setVersion("Set release version", "%releaseVersion%")

            commitAndPush(
                "Push release version",
                "build: release version %releaseVersion%",
                dryRunParameter = DRY_RUN,
            )

            setVersion("Set next snapshot version", "%nextSnapshotVersion%")

            commitAndPush(
                "Push next snapshot version",
                "build: update version to %nextSnapshotVersion%",
                dryRunParameter = DRY_RUN,
            )
          }

          artifactRules =
              """
            +:artifacts => artifacts
            +:out/jreleaser => jreleaser
            """
                  .trimIndent()

          requirements { runOnLinux(LinuxSize.SMALL) }
        },
    )
