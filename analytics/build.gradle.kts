import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.ksp)
}

kotlin {

    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.thejawnpaul.gptinvestor.analytics"
        compileSdk = 36
        minSdk = 24
        localDependencySelection {
            selectBuildTypeFrom.set(listOf("debug", "release"))
        }
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets.commonMain {
        kotlin.srcDir("build/generated/ksp/metadata/analytics")
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcfName = "analytics"

    iosX64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = xcfName
        }
    }

    cocoapods {
        version = "1.0.0"
        summary = "Analytics module for GPT Investor"
        homepage = "https://gptinvestorapp.com"
        ios.deploymentTarget = libs.versions.ios.deployment.target.get()
        framework {
            baseName = "Analytics"
            isStatic = true
        }
    }

    // Source set declarations.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.annotations)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.koin.android)
                implementation(libs.mixpanel.android)
                implementation(project.dependencies.platform(libs.firebase.compose.bom))
                implementation(libs.firebase.analaytics)
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
    val localProperties = Properties()
    localProperties.load(project.rootProject.file("local.properties").reader())
    defaultConfigs {
        buildConfigField(BOOLEAN, "DEBUG", "true")
        val mixpanelToken: String = localProperties.getProperty("MIXPANEL_DEV_TOKEN") ?: ""
        buildConfigField(STRING, "MIXPANEL_TOKEN", mixpanelToken)
    }

    defaultConfigs("release") {
        buildConfigField(BOOLEAN, "DEBUG", "false")
        val mixpanelToken: String = localProperties.getProperty("MIXPANEL_PROD_TOKEN") ?: ""
        buildConfigField(STRING, "MIXPANEL_TOKEN", mixpanelToken)
    }
}

dependencies {
    add("kspCommonMainMetadata", libs.koin.compiler)
    add("kspAndroid", libs.koin.compiler)
    add("kspIosSimulatorArm64", libs.koin.compiler)
    add("kspIosX64", libs.koin.compiler)
    add("kspIosArm64", libs.koin.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
}