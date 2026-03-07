import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.ksp)
}

android {
    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").reader())
    namespace = "com.thejawnpaul.gptinvestor.analytics"
    compileSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures { buildConfig = true }
}

dependencies {
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.annotations)
    implementation(libs.mixpanel.android)
    implementation(platform(libs.firebase.compose.bom))
    implementation(libs.firebase.analaytics)
}
