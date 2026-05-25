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
include(":domain")
include(":data")
include(":core")
include(":fastlane")
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
include(":feature:survey")
include(":feature:utils")
include(":feature:search")
include(":feature:destination")
include(":feature")
include(":feature:booking")
include(":feature:chat")
include(":core:database")
include(":feature:hotel")
