package com.thejawnpaul.gptinvestor

import android.app.Application
import com.thejawnpaul.gptinvestor.analytics.di.providesAnalyticsModule
import com.thejawnpaul.gptinvestor.remote.networkModule
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@HiltAndroidApp
class GPTInvestorApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidLogger()
            androidContext(this@GPTInvestorApplication)
            modules(listOf(providesAnalyticsModule, networkModule))
        }
    }
}
