package com.thejawnpaul.gptinvestor

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
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

        FirebaseApp.initializeApp(this)
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
