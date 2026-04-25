import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.ktLint)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.swiftklib)
}

kotlin {
    android {
        namespace = "com.thejawnpaul.gptinvestor.shared"
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

        withHostTest {
            isIncludeAndroidResources = true
        }

        withDeviceTest {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export(project(":analytics"))
            linkerOpts("-dead_strip")
        }
        iosTarget.compilations {
            val main by getting {
                cinterops {
                    create("bridges")
                }
            }
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    dependencies {
        implementation(project(":remote:remote"))

        api(project(":analytics"))
        implementation(project(":theme"))

        implementation(libs.compose.components.resources)
        implementation(libs.androidx.lifecycle.runtime.compose)
        api(libs.compose.ui)
        implementation(libs.compose.ui.tooling.preview)
        api(libs.compose.material3)
        implementation(libs.compose.material.icons.core)
        implementation(libs.compose.material.icons.extended)
        implementation(libs.androidx.navigation.compose)
        api(project.dependencies.platform(libs.coil.bom))
        api(libs.coil.compose)
        implementation(libs.coil.network.ktor)
        implementation(libs.androidx.sqlite.bundled)
        implementation(libs.androidx.room)
        api(project.dependencies.platform(libs.koin.bom))
        api(libs.koin.core)
        api(libs.koin.compose)
        implementation(libs.koin.compose.viewmodel)
        api(libs.koin.annotations)
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.http)
        implementation(libs.ktor.client.content.negotiation)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.ktor.client.logging)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
        implementation(libs.kermit)
        implementation(libs.datastore.core)
        implementation(libs.datastore.preferences)
        implementation(libs.gitlive.firebase.common)
        implementation(libs.gitlive.firebase.auth)
        implementation(libs.gitlive.firebase.config)
        implementation(libs.gitlive.firebase.messaging)
        implementation(libs.gitlive.firebase.installations)
        implementation(libs.androidx.paging.common)
        implementation(libs.androidx.paging.compose)
        implementation(libs.markdown.renderer.m3)
        implementation(libs.markdown.renderer)
    }

    sourceSets {
        androidMain {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)
                implementation(libs.kotlinx.coroutines.play.services)
                implementation(project.dependencies.platform(libs.firebase.compose.bom))
                implementation(libs.firebase.auth)
                implementation(libs.firebase.config)
                implementation(libs.firebase.messaging)
                implementation(libs.firebase.appcheck.debug)
                implementation(libs.firebase.appcheck.playintegrity)
                implementation(libs.timber)
                implementation(libs.timeAgo)
                implementation(libs.jsoup)
                implementation(libs.androidx.credentials)
                implementation(libs.androidx.play.services.auth)
                implementation(libs.androidx.browser)
                implementation(libs.google.identity)
                implementation(libs.android.billing)
                api(libs.koin.android)
                implementation(libs.ktor.client.android)
                implementation(libs.exoplayer)
                implementation(libs.exoplayer.dash)
                implementation(libs.exoplayer.ui)
                implementation(libs.exoplayer.compose)
                implementation(libs.youtube.player)
                implementation(libs.androidx.core.splashscreen)
                implementation(libs.play.app.update)
                implementation(libs.play.app.update.ktx)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutine.test)
                implementation(project(":remote:remotetest"))
                implementation(libs.google.truth)
                implementation(libs.cashapp.turbine)
            }
        }
        val androidHostTest = findByName("androidHostTest")
        androidHostTest?.dependencies {
            implementation(libs.core.ktx)
            implementation(libs.androidx.junit.ktx)
            implementation(libs.mockk)
            implementation(libs.junit)
        }
        val androidDeviceTest = findByName("androidDeviceTest")
        androidDeviceTest?.dependencies {
            implementation(libs.androidx.junit)
            implementation(libs.androidx.espresso.core)
            implementation(libs.androidx.ui.test.junit4)
        }
        iosMain {
            dependencies {
            }
        }
    }
}

compose.resources {
    packageOfResClass = "com.thejawnpaul.gptinvestor"
}

room {
    schemaDirectory("$projectDir/schemas")
}

ktlint {
    android = true
    ignoreFailures = false

    filter {
        exclude("**/build/**")
        exclude("**/generated/**")
        exclude { element ->
            element.file.path.startsWith(layout.buildDirectory.get().asFile.path)
        }
    }

    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.SARIF)
    }
    additionalEditorconfig.set(
        mapOf(
            "ktlint_code_style" to "android_studio",
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            "max_line_length" to "120"
        )
    )
}


dependencies {
    ktlintRuleset(libs.ktlint.compose.rules)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    "androidRuntimeClasspath"(libs.compose.ui.tooling)
    "androidRuntimeClasspath"(libs.androidx.ui.test.manifest)
}
buildkonfig {
    packageName = "com.thejawnpaul.gptinvestor.shared"
    objectName = "BuildConfig"

    val localProperties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.reader())
    }

    defaultConfigs {
        buildConfigField(FieldSpec.Type.BOOLEAN, "DEBUG", "false")
        buildConfigField(FieldSpec.Type.STRING, "BASE_URL", localProperties.getProperty("BASE_URL") ?: "")
        buildConfigField(FieldSpec.Type.STRING, "GEMINI_API_KEY", localProperties.getProperty("GEMINI_API_KEY") ?: "")
        buildConfigField(
            FieldSpec.Type.STRING,
            "WEB_CLIENT_ID",
            localProperties.getProperty("WEB_CLIENT_ID_PROD") ?: ""
        )
    }

    defaultConfigs("dev") {
        buildConfigField(
            FieldSpec.Type.BOOLEAN,
            "DEBUG",
            "true"
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "WEB_CLIENT_ID",
            localProperties.getProperty("WEB_CLIENT_ID_DEV") ?: ""
        )
    }
}

swiftklib {
    create("bridges") {
        path = file("../iosApp/iosApp/Bridges")
        packageName("com.thejawnpaul.gptinvestor.bridges")
    }
}
