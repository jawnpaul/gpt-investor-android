package com.thejawnpaul.gptinvestor

import android.app.Application
import com.thejawnpaul.gptinvestor.analytics.di.providesCompositeLogger
import com.thejawnpaul.gptinvestor.analytics.di.providesFirebaseLogger
import com.thejawnpaul.gptinvestor.analytics.di.providesMixpanelLogger
import com.thejawnpaul.gptinvestor.core.di.initKoin
import com.thejawnpaul.gptinvestor.core.di.providesRemoteConfig
import com.thejawnpaul.gptinvestor.remote.networkModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import timber.log.Timber

@HiltAndroidApp
class GPTInvestorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        val platformModules = listOf(
            networkModule, providesMixpanelLogger(null),
            providesFirebaseLogger(null), providesCompositeLogger(),
            providesRemoteConfig(null)
        )
        initKoin(
            config = {
                androidLogger()
                androidContext(androidContext = this@GPTInvestorApplication)
            }, platformModules = platformModules
        )
    }
}
