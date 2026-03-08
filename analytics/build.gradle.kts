import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.ksp)
}

kotlin {
    android {
        namespace = "com.thejawnpaul.gptinvestor.analytics"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }

    listOf(iosArm64(), iosSimulatorArm64()).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Analytics"
            isStatic = true
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    dependencies {
        implementation(project.dependencies.platform(libs.koin.bom))
        implementation(libs.koin.core)
        implementation(libs.koin.annotations)
        implementation(libs.gitlive.firebase.common)
        implementation(libs.gitlive.firebase.analytics)
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.mixpanel.android)
                implementation(project.dependencies.platform(libs.firebase.compose.bom))
                implementation(libs.firebase.common)
                implementation(libs.firebase.analytics)
            }
        }
        iosMain {
            dependencies {

            }
        }
    }
}

buildkonfig {
    packageName = "com.thejawnpaul.gptinvestor.analytics"
    objectName = "BuildConfig"
    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").reader())

    defaultConfigs {
        val mixpanelToken: String = localProperties.getProperty("MIXPANEL_PROD_TOKEN") ?: ""
        buildConfigField(STRING, "MIXPANEL_TOKEN", "\"$mixpanelToken\"")
    }
    defaultConfigs("dev") {
        val mixpanelToken: String = localProperties.getProperty("MIXPANEL_DEV_TOKEN") ?: ""
        buildConfigField(STRING, "MIXPANEL_TOKEN", "\"$mixpanelToken\"")
    }
}
