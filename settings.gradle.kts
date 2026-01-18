pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Travio"
include(":app")
include(":core")
include(":domain")
include(":data")
include(":core:network")
include(":core:designsystem")
include(":core:common")
include(":feature:auth")
include(":feature:ai")
include(":feature:community")
include(":feature:home")
include(":feature:favorite")
include(":feature:onboarding")
include(":feature:profile")
include(":feature:utils")
include(":feature")
include(":di")
