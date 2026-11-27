package builds

import jetbrains.buildServer.configs.kotlin.Project
import jetbrains.buildServer.configs.kotlin.sequential
import jetbrains.buildServer.configs.kotlin.toId
import jetbrains.buildServer.configs.kotlin.triggers.vcs

class Build(
    name: String,
    branchFilter: String,
    forPullRequests: Boolean,
    triggerRules: String? = null
) :
    Project({
      this.id(name.toId())
      this.name = name

      val complete = Empty("${name}-complete", "complete")

      val bts = sequential {
        if (forPullRequests) {
          parallel {
            dependentBuildType(SemgrepCheck("${name}-semgrep-check", "semgrep check"))
            dependentBuildType(
                VerifyMavenBuild(
                    "${name}-build",
                    "build",
                    "verify",
                    javaVersion = DEFAULT_JAVA_VERSION,
                ))
          }
        } else {
          dependentBuildType(
              VerifyMavenBuild(
                  "${name}-build",
                  "build",
                  "verify",
                  javaVersion = DEFAULT_JAVA_VERSION,
              ))
        }

        dependentBuildType(complete)
        if (!forPullRequests) dependentBuildType(Release("${name}-release", "release"))
      }

      bts.buildTypes().forEach {
        it.thisVcs()

        it.features {
          loginToECR()
          enableCommitStatusPublisher()
          if (forPullRequests) enablePullRequests()
        }

        buildType(it)
      }

      complete.triggers {
        vcs {
          this.branchFilter = branchFilter
          this.triggerRules = triggerRules
        }
      }
    })
