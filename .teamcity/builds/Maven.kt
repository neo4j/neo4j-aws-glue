package builds

import jetbrains.buildServer.configs.kotlin.BuildType
import jetbrains.buildServer.configs.kotlin.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.toId

class Maven(
    id: String,
    name: String,
    goals: String,
    args: String? = null,
    javaVersion: String = DEFAULT_JAVA_VERSION,
    size: LinuxSize = LinuxSize.SMALL,
    init: BuildType.() -> Unit = {},
) :
    BuildType({
      this.id(id.toId())
      this.name = name

      steps {
        runMaven(javaVersion) {
          this.goals = goals
          this.runnerArgs = "$MAVEN_DEFAULT_ARGS ${args ?: ""}"
        }
        sha256("sha256 sum")
      }

      features { dockerSupport {} }

      requirements { runOnLinux(size) }

      this.init()

      artifactRules =
        """
        +:target/neo4j-aws-glue*jar
        +:target/neo4j-aws-glue*.sha256
        """
            .trimIndent()
    })
