buildscript {
    val android_sdk_compile by extra(32)
    val android_sdk_min by extra(23)
    val android_sdk_target by extra(32)
    val compose_version by extra("1.2.0-beta02")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.2.1" apply false
    id("com.android.library") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}