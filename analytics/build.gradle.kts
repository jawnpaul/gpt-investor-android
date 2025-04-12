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
    namespace = "com.thejawnpaul.gptinvestor.analytics"
    compileSdk = 34
    defaultConfig.minSdk = 24
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
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.mixpanel.android)
    implementation(platform(libs.firebase.compose.bom))
    implementation(libs.firebase.analaytics)
}
