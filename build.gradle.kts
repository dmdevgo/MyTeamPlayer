buildscript {
    val android_sdk_compile by extra(32)
    val android_sdk_min by extra(26)
    val android_sdk_target by extra(32)
    val compose_version by extra("1.2.0-alpha07")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.2.1" apply false
    id("com.android.library") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.6.10" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10" apply false
    id("org.jetbrains.compose") version "1.1.1" apply false
}
