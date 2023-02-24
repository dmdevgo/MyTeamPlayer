buildscript {
    val android_sdk_compile by extra(32)
    val android_sdk_min by extra(23)
    val android_sdk_target by extra(32)
    val compose_version by extra("1.2.0-rc02")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.1" apply false
    id("com.android.library") version "7.4.1" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21" apply false
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev731" apply false
}
