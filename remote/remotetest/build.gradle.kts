plugins {
    id("com.android.library")
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.thejawnpaul.gptinvestor"
    compileSdk = 36
    defaultConfig.minSdk = 24
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { buildConfig = true }
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.dagger.hilt)
    implementation(libs.retrofit)
    implementation(libs.moshi.converter)
    implementation(libs.okhttp.logger)
    ksp(libs.dagger.hilt.compiler)
}