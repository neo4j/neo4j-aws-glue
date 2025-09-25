import builds.Build
import jetbrains.buildServer.configs.kotlin.project
import jetbrains.buildServer.configs.kotlin.version

version = "2025.07"

project {
  params {
    password("github-commit-status-token", "%github-token%")
  }

  subProject(
      Build(
          name = "main",
          branchFilter =
              """
                +:main
              """
                  .trimIndent(),
          triggerRules =
              """
                -:comment=^build.*release version.*:**
                -:comment=^build.*update version.*:**
              """
                  .trimIndent()))
}
