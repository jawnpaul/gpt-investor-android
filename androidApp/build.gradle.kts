import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.koin.compiler)
}

val keystoreProperties = Properties()
keystoreProperties.load(project.rootProject.file("keystore.properties").reader())

kotlin {
    target {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    dependencies {
        implementation(project(":composeApp"))
        implementation(project.dependencies.platform(libs.firebase.compose.bom))
        implementation(libs.firebase.crashlytics)
        implementation(libs.firebase.appcheck)
        implementation(libs.firebase.appcheck.debug)
        implementation(libs.firebase.appcheck.playintegrity)

        implementation(libs.timber)
        implementation(libs.firebase.messaging)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.core.splashscreen)
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.play.app.update)
        implementation(libs.play.app.update.ktx)
        implementation(libs.compose.runtime)
        implementation(libs.compose.foundation)
    }
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
        versionCode = 29
        versionName = "1.1.8"

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

            val baseUrl: String = localProperties.getProperty("BASE_URL_DEV") ?: ""
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
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

tasks.register<Copy>("installGitHook") {
    from(file("${rootProject.rootDir}/scripts/pre-commit"))
    into(file("${rootProject.rootDir}/.git/hooks"))
}

tasks.getByPath(":androidApp:preBuild").dependsOn("installGitHook")
