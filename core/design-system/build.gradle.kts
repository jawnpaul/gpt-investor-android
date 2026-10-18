import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.ktLint)
}

kotlin {
    android {
        namespace = "com.thejawnpaul.gptinvestor.theme"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilations.all {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
            }
        }

        androidResources {
            enable = true
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Theme"
            isStatic = true
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    dependencies {
        implementation(libs.compose.runtime)
        implementation(libs.compose.foundation)
        implementation(libs.compose.material3)
        implementation(libs.compose.ui)
        implementation(libs.compose.components.resources)
        implementation(libs.compose.ui.tooling.preview)
        implementation(project.dependencies.platform(libs.coil.bom))
        implementation(libs.coil.compose)
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
    }
}

compose.resources {
    packageOfResClass = "com.thejawnpaul.gptinvestor.theme"
}

ktlint {
    android = true
    ignoreFailures = false

    // Only the v4 files for now; the pre-v4 theme files predate ktlint in this module.
    filter {
        exclude { element ->
            element.file.path.contains("/build/") || (!element.isDirectory && !element.file.name.startsWith("Gi"))
        }
    }

    additionalEditorconfig.set(
        mapOf(
            "ktlint_code_style" to "android_studio",
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            "max_line_length" to "120",
            "compose_allowed_composition_locals" to "LocalGiColors,LocalGiType"
        )
    )
}

dependencies {
    ktlintRuleset(libs.ktlint.compose.rules)
    "androidRuntimeClasspath"(libs.compose.ui.tooling)
}
