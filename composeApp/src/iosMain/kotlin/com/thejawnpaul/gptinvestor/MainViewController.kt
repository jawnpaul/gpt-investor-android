package com.thejawnpaul.gptinvestor

import androidx.compose.ui.window.ComposeUIViewController
import com.thejawnpaul.gptinvestor.core.di.GPTKoinApp
import org.koin.compose.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController = ComposeUIViewController {
    KoinApplication(
        configuration = koinConfiguration<GPTKoinApp> {
            printLogger()
            allowOverride(true)
        }
    ) {
        App()
    }
}
