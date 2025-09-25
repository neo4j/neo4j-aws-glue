package builds

import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.sequential
import jetbrains.buildServer.configs.kotlin.toId
import jetbrains.buildServer.configs.kotlin.triggers.vcs

class Build(
    name: String,
    branchFilter: String,
    triggerRules: String? = null
) :
    Project({
      this.id(name.toId())
      this.name = name

      val complete = Empty("${name}-complete", "complete")

      val bts = sequential {

        dependentBuildType(
            VerifyMavenBuild(
                "${name}-build",
                "build",
                "verify",
                javaVersion = DEFAULT_JAVA_VERSION,
            ))
      }

      bts.buildTypes().forEach {
        it.thisVcs()

        buildType(it)
      }

      complete.triggers {
        vcs {
          this.branchFilter = branchFilter
          this.triggerRules = triggerRules
        }
      }
    })
