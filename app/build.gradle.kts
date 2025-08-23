@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktLint)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.room)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "GPT_Investor"
            isStatic = true
            export(project(":analytics"))
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.dagger.hilt)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.hilt.navigation)
            implementation(libs.retrofit)
            implementation(project.dependencies.platform(libs.firebase.compose.bom))
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.config)
            implementation(libs.firebase.messaging)
            implementation(libs.firebase.ai)
        }

        androidUnitTest.dependencies {
            implementation(project(":remote:remotetest"))
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(project(":remote:remote"))
            api(project(":analytics"))
            implementation(project(":theme"))
            implementation(libs.bundles.ksoup)
            implementation(libs.datastore.preferences)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.big.decimal)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.timeAgo)
            implementation(libs.kermit)
        }

        commonTest.dependencies {
        }

        nativeMain.dependencies {
        }
        all {
            languageSettings.enableLanguageFeature("PropertyParamAnnotationDefaultTargetMode")
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
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
        targetSdk = 34
        versionCode = 10
        versionName = "1.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.timber)
    implementation(libs.coil.compose)
    implementation(libs.core.ktx)
    implementation(libs.androidx.junit.ktx)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.richtext.compose)
    implementation(libs.richtext.commonmark)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.play.services.auth)
    implementation(libs.google.identity)

    // test
    kspTest(libs.dagger.hilt.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.google.truth)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutine.test)
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    // Force the version
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains:annotations:23.0.0")
            exclude(group = "com.intellij", module = "annotations")
        }
    }
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.test.manifest)
}
