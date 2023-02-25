@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = rootProject.extra["android_sdk_compile"] as Int

    defaultConfig {
        applicationId = "me.dmdev.myteamplayer"
        minSdk = rootProject.extra["android_sdk_min"] as Int
        targetSdk = rootProject.extra["android_sdk_target"] as Int
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
        }

        getByName("test") {
            java.srcDirs("src/test/kotlin")
        }
    }

    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
}

tasks.register<Copy>("CopyWebFolder") {
    val webProject = project(":app-client-web")
    val androidProject = project(":app-client-android")
    val taskName = if (webProject.hasProperty("isProduction")
        || webProject.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = webProject.tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask)
    dependsOn(androidProject.tasks.getByName("assemble"))
    from(
        File(webpackTask.destinationDirectory, webpackTask.outputFileName),
        File(webProject.projectDir, "src/jsMain/resources/index.html"),
        File(androidProject.projectDir, "build/outputs/apk/debug/app-client-android-debug.apk")
    )
    into("src/main/assets/web/")
}

tasks.register<Delete>("DeleteWebFolder") {
    delete("src/main/assets/web/")
}

tasks.getByName("assemble") {
    dependsOn(tasks.getByName("CopyWebFolder"))
}

tasks.getByName("clean") {
    dependsOn(tasks.getByName("DeleteWebFolder"))
}

dependencies {
    implementation(project(":app-common-models"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.leanback)
    implementation(libs.androidx.media2.session)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.partial.content)
    implementation(libs.ktor.server.auto.head.response)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidyoutubeplayer)
}
