package com.thejawnpaul.gptinvestor

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.thejawnpaul.gptinvestor.core.di.GPTKoinApp
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinApplication
import org.koin.plugin.module.dsl.koinConfiguration

class MainActivity : ComponentActivity() {

    private val deepLinkRouteState = mutableStateOf<String?>(null)

    private lateinit var appUpdateManager: AppUpdateManager

    private val updateResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                println("Update flow failed! Result code: ${'$'}{result.resultCode}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdates()

        handleDeepLinkFromIntent(intent) { route ->
            deepLinkRouteState.value = route
        }

        setContent {
            var deepLinkRoute by deepLinkRouteState
            KoinApplication(configuration = koinConfiguration<GPTKoinApp> {
                printLogger()
                androidContext(this@MainActivity)
            }) {
                App(
                    deepLinkRoute = deepLinkRoute,
                    onDeepLinkConsume = { deepLinkRoute = null }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let { newIntent ->
            setIntent(newIntent)
            handleDeepLinkFromIntent(newIntent) { route ->
                lifecycleScope.launch {
                    deepLinkRouteState.value = route
                }
            }
        }
    }

    private fun handleDeepLinkFromIntent(intent: Intent?, onDeepLink: (String) -> Unit) {
        intent?.let {
            val deepLinkRoute = it.getStringExtra("deep_link")
            val notificationData = it.getStringExtra("notification_data")

            deepLinkRoute?.let { route ->
                onDeepLink(route)
            }

            notificationData?.let { data ->
                println("Notification data: ${'$'}data")
            }
        }
    }

    private fun checkForUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() ==
                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }
        }
    }
}
