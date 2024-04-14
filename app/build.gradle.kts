import java.util.Properties
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ktLint)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.crashlytics)
}

android {

    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").reader())

    namespace = "com.thejawnpaul.gptinvestor"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.thejawnpaul.gptinvestor"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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

    buildTypes {
        release {
            val geminiApiKey: String = localProperties.getProperty("GEMINI_API_KEY")
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")

            val baseUrl: String = localProperties.getProperty("BASE_URL")
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            val geminiApiKey: String = localProperties.getProperty("GEMINI_DEBUG_KEY")
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")

            val baseUrl: String = localProperties.getProperty("BASE_URL")
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

            isShrinkResources = false
            isMinifyEnabled = false
            versionNameSuffix = "-dev"
            isDebuggable = true
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

tasks.getByPath("preBuild").dependsOn("ktlintFormat")

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
            "max_line_length" to "140"
        )
    )
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
    implementation(libs.dagger.hilt)
    implementation(libs.timber)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation)
    implementation(libs.retrofit)
    implementation(libs.moshi.converter)
    implementation(libs.okhttp.logger)
    implementation(libs.coil.compose)
    kapt(libs.dagger.hilt.compiler)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.timeAgo)
    implementation(libs.jsoup)
    implementation(libs.gemini)
    implementation(libs.richtext.compose)
    implementation(libs.richtext.commonmark)
    implementation(platform(libs.firebase.compose.bom))
    implementation(libs.firebase.analaytics)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
