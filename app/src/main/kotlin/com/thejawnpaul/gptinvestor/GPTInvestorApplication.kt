package com.thejawnpaul.gptinvestor

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.thejawnpaul.gptinvestor.core.di.AppModule
import com.thejawnpaul.gptinvestor.features.notification.domain.TokenSyncManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin
import timber.log.Timber

@KoinApplication(modules = [AppModule::class])
class GPTInvestorApplication : Application() {

    private val tokenSyncManager: TokenSyncManager by inject()

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        startKoin<GPTInvestorApplication> {
            androidLogger()
            androidContext(this@GPTInvestorApplication)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
        tokenSyncManager.syncToken()
    }
}
