import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    android {
        namespace = "com.thejawnpaul.gptinvestor.remote"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Remote"
            isStatic = true
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    dependencies {
        implementation(project.dependencies.platform(libs.koin.bom))
        implementation(libs.koin.core)
        implementation(libs.koin.compose)
        implementation(libs.koin.annotations)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.ktor.client.logging)
        implementation(libs.ktor.client.auth)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kermit)
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.ktor.client.android)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

buildkonfig {
    packageName = "com.thejawnpaul.gptinvestor.remote"
    objectName = "BuildConfig"
    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").reader())
    defaultConfigs {
        val baseUrl: String = localProperties.getProperty("BASE_URL_DEV") ?: ""
        buildConfigField(BOOLEAN, "DEBUG", "true")
        buildConfigField(STRING, "BASE_URL", baseUrl)
    }

    defaultConfigs("release") {
        val baseUrl: String = localProperties.getProperty("BASE_URL") ?: ""
        buildConfigField(BOOLEAN, "DEBUG", "false")
        buildConfigField(STRING, "BASE_URL", baseUrl)
    }
}


