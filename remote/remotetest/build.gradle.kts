import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
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
        kotlin.srcDir("build/generated/ksp/metadata/remotetest")
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
            baseName = "RemoteTest"
            isStatic = true
        }
    }

    sourceSets {

        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.ktorfit)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
    }
}

android {
    namespace = "com.thejawnpaul.gptinvestor"
    compileSdk = 36
    defaultConfig.minSdk = 24
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
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