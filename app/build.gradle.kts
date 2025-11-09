@file:Suppress("UnstableApiUsage")

import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.ktLint)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
}

val keystoreProperties = Properties()
keystoreProperties.load(project.rootProject.file("keystore.properties").reader())

android {

    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").reader())

    namespace = "com.thejawnpaul.gptinvestor"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.thejawnpaul.gptinvestor"
        minSdk = 24
        targetSdk = 36
        versionCode = 14
        versionName = "1.1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
        }
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties.getProperty("KEY_ALIAS") ?: ""
            keyPassword = keystoreProperties.getProperty("KEY_PASSWORD") ?: ""
            storeFile = file(keystoreProperties.getProperty("STORE_FILE") ?: "")
            storePassword = keystoreProperties.getProperty("KEY_STORE_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")

            val geminiApiKey: String = localProperties.getProperty("GEMINI_API_KEY") ?: ""
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")

            val baseUrl: String = localProperties.getProperty("BASE_URL") ?: ""
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

            val accessToken: String = localProperties.getProperty("ACCESS_TOKEN") ?: ""
            buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")

            val webClientId: String = localProperties.getProperty("WEB_CLIENT_ID_PROD") ?: ""
            buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")

            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            val geminiApiKey: String = localProperties.getProperty("GEMINI_DEBUG_KEY") ?: ""
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")

            val baseUrl: String = localProperties.getProperty("BASE_URL") ?: ""
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

            val accessToken: String = localProperties.getProperty("ACCESS_TOKEN") ?: ""
            buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")

            val webClientId: String = localProperties.getProperty("WEB_CLIENT_ID_DEV") ?: ""
            buildConfigField("String", "WEB_CLIENT_ID", "\"$webClientId\"")

            applicationIdSuffix = ".dev"
            isShrinkResources = false
            isMinifyEnabled = false
            versionNameSuffix = "-dev"
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

ktlint {
    android = true
    ignoreFailures = false
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.SARIF)
    }
    additionalEditorconfig.set(
        mapOf(
            "ktlint_code_style" to "android_studio",
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            "max_line_length" to "190"
        )
    )
}

tasks.register<Copy>("installGitHook") {
    from(file("${rootProject.rootDir}/scripts/pre-commit"))
    into(file("${rootProject.rootDir}/.git/hooks"))
}

tasks.getByPath(":app:preBuild").dependsOn("installGitHook")

dependencies {
    implementation(project(":remote:remote"))
    implementation(project(":theme"))
    implementation(project(":analytics"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation("org.jetbrains.compose.components:components-resources:1.7.1")
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.icons.extended)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.timber)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.core.ktx)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.timeAgo)
    implementation(libs.jsoup)
    implementation(libs.richtext.compose)
    implementation(libs.richtext.commonmark)
    implementation(platform(libs.firebase.compose.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.ai)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.play.services.auth)
    implementation(libs.google.identity)
    implementation(libs.firebase.messaging)
    implementation(libs.exoplayer)
    implementation(libs.exoplayer.dash)
    implementation(libs.exoplayer.ui)
    implementation(libs.exoplayer.compose)
    implementation(libs.youtube.player)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.start.up)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewmodel)
    ksp(libs.koin.compiler)
    implementation(libs.ktorfit)
    ksp(libs.ktorfit.compiler)
    implementation(libs.kotlinx.serialization)

    // test
    testImplementation(project(":remote:remotetest"))
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
