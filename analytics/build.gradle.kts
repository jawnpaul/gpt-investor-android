@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.reader())
}

kotlin {
    androidTarget {
        compilerOptions {
           jvmTarget.set(JvmTarget.JVM_17)
        }
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "GPT-Investor-Analytics"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.firebase.analaytics)

            implementation(libs.mixpanel.android)
        }

        androidUnitTest.dependencies {
            implementation(libs.koin.test) // Or JUnit5
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json) // If used

            // Koin Annotations
            api(libs.koin.annotations)
            implementation(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.koin.test)
        }

        // Example: iOS source set
        nativeMain.dependencies {

        }
    }

    // If using Cocoapods for iOS dependencies
    // cocoapods { /* ... */ }
}


android {
    namespace = "com.thejawnpaul.gptinvestor.analytics"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    buildTypes {
        release {
            val mixpanelToken: String = localProperties.getProperty("MIXPANEL_PROD_TOKEN") ?: ""
            buildConfigField("String", "MIXPANEL_TOKEN", "\"$mixpanelToken\"")
        }
        debug {
            val mixpanelToken: String = localProperties.getProperty("MIXPANEL_DEV_TOKEN") ?: ""
            buildConfigField("String", "MIXPANEL_TOKEN", "\"$mixpanelToken\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.firebase.compose.bom))
}
