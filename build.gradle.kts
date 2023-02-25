buildscript {
    val android_sdk_compile by extra(33)
    val android_sdk_min by extra(23)
    val android_sdk_target by extra(33)
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.3.0" apply false
    id("com.android.library") version "7.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20" apply false
    id("org.jetbrains.compose") version "1.3.0" apply false
}
