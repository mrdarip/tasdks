// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.apply {
        set("room_version", "2.5.2")
    }
}

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.compose.compiler) apply false  // Added Compose Compiler plugin
    id("com.google.devtools.ksp") version "1.9.0-1.0.12" apply false
}