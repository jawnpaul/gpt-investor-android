package com.thejawnpaul.gptinvestor

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.thejawnpaul.gptinvestor.core.di.GPTKoinApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.plugin.module.dsl.koinConfiguration

class GPTInvestorApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(
            koinConfiguration<GPTKoinApp> {
                androidContext(this@GPTInvestorApplication)
            }
        )

        FirebaseApp.initializeApp(this)

        if (BuildConfig.DEBUG) {
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        } else {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        }
//        tokenSyncManager.syncToken()
    }
}
