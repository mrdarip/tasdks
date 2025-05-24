plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.0.21"
}

// Add function to get Git information
fun getGitInfo(): Map<String, String> {
    val result = HashMap<String, String>()
    try {
        val commitHashCommand = Runtime.getRuntime().exec("git rev-parse --short HEAD")
        val commitHash = commitHashCommand.inputStream.bufferedReader().readText().trim()
        result["commitHash"] = commitHash

        val commitMsgCommand = Runtime.getRuntime().exec("git log -1 --pretty=%B")
        val commitMsg = commitMsgCommand.inputStream.bufferedReader().readText().trim()
        result["commitMsg"] = commitMsg

        val branchCommand = Runtime.getRuntime().exec("git rev-parse --abbrev-ref HEAD")
        val branch = branchCommand.inputStream.bufferedReader().readText().trim()
        result["branch"] = branch
    } catch (e: Exception) {
        result["commitHash"] = "unknown"
        result["commitMsg"] = "unknown"
        result["branch"] = "unknown"
    }
    return result
}

android {
    namespace = "com.mrdarip.tasdks"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mrdarip.tasdks"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Add Git info to BuildConfig
        val gitInfo = getGitInfo()
        buildConfigField("String", "GIT_COMMIT_HASH", "\"${gitInfo["commitHash"]}\"")
        buildConfigField("String", "GIT_COMMIT_MSG", "\"${gitInfo["commitMsg"]}\"")
        buildConfigField("String", "GIT_BRANCH", "\"${gitInfo["branch"]}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true  // Enable BuildConfig generation
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.room.common)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    //Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.core.ktx.v1120)
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation(libs.androidx.room.ktx)

    //icons
    implementation(libs.androidx.material.icons.extended.v176)

    //for dialogs
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)

}