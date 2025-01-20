import java.util.Properties

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ksp)
}

android {
    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").reader())
    namespace = "com.thejawnpaul.gptinvestor"
    compileSdk = 34
    defaultConfig.minSdk = 24
    buildTypes {
        release {
            val baseUrl: String = localProperties.getProperty("BASE_URL") ?: ""
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
            val accessToken: String = localProperties.getProperty("ACCESS_TOKEN") ?: ""
            buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")
        }
        debug {
            val baseUrl: String = localProperties.getProperty("BASE_URL") ?: ""
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
            val accessToken: String = localProperties.getProperty("ACCESS_TOKEN") ?: ""
            buildConfigField("String", "ACCESS_TOKEN", "\"$accessToken\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation(libs.dagger.hilt)
    implementation(libs.retrofit)
    implementation(libs.moshi.converter)
    implementation(libs.okhttp.logger)
    ksp(libs.dagger.hilt.compiler)
}
