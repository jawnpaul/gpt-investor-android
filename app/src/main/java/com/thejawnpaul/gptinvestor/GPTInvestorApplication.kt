package com.thejawnpaul.gptinvestor

import android.app.Application
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import com.thejawnpaul.gptinvestor.remote.RemoteModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.koinConfiguration
import org.koin.ksp.generated.module
import timber.log.Timber
import javax.inject.Inject

@OptIn(KoinExperimentalAPI::class)
@HiltAndroidApp
class GPTInvestorApplication :
    Application(),
    KoinStartup {

    override fun onKoinStartup(): KoinConfiguration = koinConfiguration {
        androidLogger()
        modules(RemoteModule.module)
    }

    @Inject
    lateinit var tokenSyncManager: TokenSyncManager

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        tokenSyncManager.syncToken()
    }
}
