package com.thejawnpaul.gptinvestor

import com.thejawnpaul.gptinvestor.analytics.mixpanel.MixpanelProvider
import com.thejawnpaul.gptinvestor.analytics.mixpanel.mixpanelProviderModule
import com.thejawnpaul.gptinvestor.core.di.GPTKoinApp
import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider
import com.thejawnpaul.gptinvestor.core.platform.YoutubePlayerProvider
import com.thejawnpaul.gptinvestor.core.platform.youtubePlayerProviderModule
import com.thejawnpaul.gptinvestor.features.authentication.domain.googleSignInProviderModule
import org.koin.core.context.startKoin
import org.koin.plugin.module.dsl.koinConfiguration

fun initKoin(
    mixpanelProvider: MixpanelProvider,
    youtubePlayerProvider: YoutubePlayerProvider,
    googleSignInProvider: GoogleSignInProvider
) {
    startKoin(
        koinConfiguration<GPTKoinApp> {
            printLogger()
            allowOverride(true)
            modules(
                mixpanelProviderModule(mixpanelProvider),
                youtubePlayerProviderModule(youtubePlayerProvider),
                googleSignInProviderModule(googleSignInProvider)
            )
        }
    )
}
