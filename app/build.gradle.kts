@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.ktLint)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.room)
}

val keystoreProperties = Properties()
keystoreProperties.load(project.rootProject.file("keystore.properties").reader())

kotlin {

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "GPTInvestor"
            isStatic = true
            freeCompilerArgs += "-Xbinary=bundleId=com.thejawnpaul.gptinvestor.GPTInvestor"
        }
    }

    cocoapods {
        version = "1.0.0"
        summary = "GPT Investor"
        homepage = "https://gptinvestorapp.com"
        ios.deploymentTarget = libs.versions.ios.deployment.target.get()
        name = "GPTInvestorApp"
        framework {
            baseName = "GPTInvestorApp"
            isStatic = true
//            export(project(":analytics"))
            transitiveExport = false
        }
        pod("FirebaseAuth") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseMessaging") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseRemoteConfig") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseCrashlytics") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        pod("FirebaseAILogic") {
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.start.up)
            implementation(libs.koin.android)
            implementation(project.dependencies.platform(libs.firebase.compose.bom))
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.ai)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.config)
            implementation(libs.firebase.messaging)
            implementation(compose.preview)
            implementation(libs.exoplayer)
            implementation(libs.exoplayer.dash)
            implementation(libs.exoplayer.ui)
            implementation(libs.exoplayer.compose)
            implementation(libs.youtube.player)
            implementation(libs.ktor.android)
            implementation(compose.uiTooling)
            implementation(libs.androidx.paging.runtime.ktx)
        }
        commonMain.dependencies {
            implementation(project(":remote:remote"))
            implementation(project(":theme"))
            implementation(project(":analytics"))
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.navigation.compose)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.preview)
            implementation(libs.bundles.ksoup)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network)
            implementation(libs.ktorfit)
            implementation(libs.kotlinx.serialization)
            implementation(libs.datastore.preferences)
            implementation(libs.kotlinx.datetime)
            implementation(libs.big.decimal)
            implementation(libs.androidx.room)
            implementation(libs.sqlite.bundled)
            implementation(libs.timeAgo)
            implementation(libs.kermit)
            implementation(libs.androidx.paging.compose)
        }

        commonTest.dependencies {
            implementation(project(":remote:remotetest"))
        }

        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }

        androidUnitTest.dependencies {
            implementation(libs.junit)
            implementation(libs.google.truth)
            implementation(libs.okhttp.mockwebserver)
            implementation(libs.mockk)
            implementation(libs.coroutine.test)
        }

        androidInstrumentedTest.dependencies {
            implementation(libs.androidx.junit)
            implementation(libs.androidx.espresso.core)
            implementation(project.dependencies.platform(libs.androidx.compose.bom))
            implementation(libs.androidx.ui.test.junit4)
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

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
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.core.ktx)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.richtext.compose)
    implementation(libs.richtext.commonmark)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.play.services.auth)
    implementation(libs.google.identity)

    add("kspCommonMainMetadata", libs.koin.compiler)
    add("kspAndroid", libs.koin.compiler)
    add("kspIosSimulatorArm64", libs.koin.compiler)
    add("kspIosX64", libs.koin.compiler)
    add("kspIosArm64", libs.koin.compiler)

    add("kspCommonMainMetadata", libs.ktorfit.compiler)
    add("kspAndroid", libs.ktorfit.compiler)
    add("kspIosSimulatorArm64", libs.ktorfit.compiler)
    add("kspIosX64", libs.ktorfit.compiler)
    add("kspIosArm64", libs.ktorfit.compiler)

    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)

    debugImplementation(libs.androidx.ui.test.manifest)
}
