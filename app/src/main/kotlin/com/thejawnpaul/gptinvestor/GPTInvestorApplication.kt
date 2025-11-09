package com.thejawnpaul.gptinvestor

import android.app.Application
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class GPTInvestorApplication : Application() {

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
