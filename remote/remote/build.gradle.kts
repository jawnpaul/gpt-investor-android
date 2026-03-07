import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").reader())
    namespace = "com.thejawnpaul.gptinvestor.remote"
    compileSdk = 36
    defaultConfig.minSdk = 24
    buildTypes {
        release {
            val baseUrl: String = localProperties.getProperty("BASE_URL") ?: ""
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        }
        debug {
            val baseUrl: String = localProperties.getProperty("BASE_URL_DEV") ?: ""
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.annotations)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
}


