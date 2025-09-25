package builds

import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.buildSteps.MavenBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.ScriptBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.script

const val GITHUB_OWNER = "neo4j"
const val GITHUB_REPOSITORY = "neo4j-aws-glue"
const val MAVEN_DEFAULT_ARGS = "--no-transfer-progress --batch-mode --show-version"

const val DEFAULT_JAVA_VERSION = "17"

enum class LinuxSize(val value: String) {
  SMALL("small"),
  LARGE("large")
}

fun Requirements.runOnLinux(size: LinuxSize = LinuxSize.SMALL) {
  startsWith("cloud.amazon.agent-name-prefix", "linux-${size.value}")
}

fun BuildType.thisVcs() = vcs {
  root(DslContext.settingsRoot)

  cleanCheckout = true
}

fun CompoundStage.dependentBuildType(bt: BuildType) =
    buildType(bt) {
      onDependencyCancel = FailureAction.CANCEL
      onDependencyFailure = FailureAction.FAIL_TO_START
    }

fun BuildSteps.runMaven(
    javaVersion: String = DEFAULT_JAVA_VERSION,
    init: MavenBuildStep.() -> Unit
): MavenBuildStep {
  val maven =
      this.maven {
        dockerImagePlatform = MavenBuildStep.ImagePlatform.Linux
        dockerImage = "eclipse-temurin:${javaVersion}-jdk"
        dockerRunParameters = "--volume /var/run/docker.sock:/var/run/docker.sock"
      }

  init(maven)
  return maven
}

fun BuildSteps.sha256(
    name: String
): ScriptBuildStep {
    return this.script {
        this.name = name
        scriptContent =
            """
            #!/bin/bash -eu
            FILE_NAME=$(ls target/neo4j-aws-glue*jar)
            sha256sum ${'$'}FILE_NAME | awk -F' ' '{print $1}' > ${'$'}FILE_NAME.sha256
            """
                .trimIndent()
    }
}

fun BuildSteps.setVersion(name: String, version: String): MavenBuildStep {
  return this.runMaven {
    this.name = name
    goals = "versions:set"
    runnerArgs = "$MAVEN_DEFAULT_ARGS -DnewVersion=$version -DgenerateBackupPoms=false"
  }
}

fun BuildSteps.mavenPackage(name: String): MavenBuildStep {
    return this.runMaven {
        this.name = name
        goals = "package"
        runnerArgs = MAVEN_DEFAULT_ARGS
    }
}

fun BuildSteps.commitAndPush(
    name: String,
    commitMessage: String,
    includeFiles: String = "\\*pom.xml",
): ScriptBuildStep {
  return this.script {
    this.name = name
    scriptContent =
        """
          #!/bin/bash -eu              
         
          git add $includeFiles
          git commit -m "$commitMessage"
          git push
        """
            .trimIndent()

  }
}
