package com.thejawnpaul.gptinvestor

import android.app.Application
import com.thejawnpaul.gptinvestor.analytics.di.analyticsModule
import com.thejawnpaul.gptinvestor.core.coreModules
import com.thejawnpaul.gptinvestor.features.authentication.di.authModule
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.features.useCaseModules
import com.thejawnpaul.gptinvestor.features.viewModelModule
import com.thejawnpaul.gptinvestor.remote.RemoteModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import org.koin.ksp.generated.module

@OptIn(KoinExperimentalAPI::class)
class GPTInvestorApplication :
    Application(), KoinComponent,
    KoinStartup {

    override fun onKoinStartup(): KoinConfiguration = koinConfiguration {
        androidContext(this@GPTInvestorApplication)
        androidLogger()
        modules(
            listOf(
                RemoteModule.module,
                analyticsModule,
                authModule
            ) + coreModules + viewModelModule + useCaseModules
        )
    }

    private val tokenSyncManager: TokenSyncManager by inject()

    override fun onCreate() {
        super.onCreate()
        tokenSyncManager.syncToken()
    }
}
