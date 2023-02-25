@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}
rootProject.name = "MyTeamPlayer"
include(":app-tv-server")
include(":app-client-common")
include(":app-client-android")
include(":app-common-models")
include(":app-client-web")
