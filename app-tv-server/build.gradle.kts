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
    val ktorVersion = "2.0.1"
    implementation(project(":app-common-models"))
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.media2:media2-session:1.2.1")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-partial-content:$ktorVersion")
    implementation("io.ktor:ktor-server-auto-head-response:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.0.1")
}
