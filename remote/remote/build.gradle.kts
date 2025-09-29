import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata/remote")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "Remote module for GPT Investor"
        homepage = "https://gptinvestorapp.com"
        ios.deploymentTarget = libs.versions.ios.deployment.target.get()
        framework {
            baseName = "Remote"
            isStatic = true
        }
    }

    sourceSets {

        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.annotations)
            implementation(libs.ktorfit)
            implementation(libs.kotlinx.serialization)
            implementation(libs.ktor.auth)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
    }

}

buildkonfig {
    packageName = "com.thejawnpaul.gptinvestor.remote"
    defaultConfigs {
        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").reader())
        val baseUrl: String = localProperties.getProperty("BASE_URL") ?: ""
        val accessToken: String = localProperties.getProperty("ACCESS_TOKEN") ?: ""
        buildConfigField(STRING, "BASE_URL", baseUrl)
        buildConfigField(STRING, "ACCESS_TOKEN", accessToken)
    }
}

android {
    namespace = "com.thejawnpaul.gptinvestor.remote"
    compileSdk = 36
    defaultConfig.minSdk = 24
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { buildConfig = true }
}

dependencies {
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
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}
