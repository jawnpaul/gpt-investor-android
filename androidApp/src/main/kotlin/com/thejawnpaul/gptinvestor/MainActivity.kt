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
import com.thejawnpaul.gptinvestor.analytics.AnalyticsLogger
import com.thejawnpaul.gptinvestor.core.platform.ActivityContextHolder
import com.thejawnpaul.gptinvestor.features.notification.analytics.EVENT_NOTIFICATION_OPENED
import com.thejawnpaul.gptinvestor.features.notification.analytics.notificationOpenedParams
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val deepLinkRouteState = mutableStateOf<String?>(null)

    private val analyticsLogger: AnalyticsLogger by inject()

    private lateinit var appUpdateManager: AppUpdateManager

    private var isAppReady = false

    private val updateResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                println("Update flow failed! Result code: ${'$'}{result.resultCode}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { !isAppReady }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ActivityContextHolder.set(this)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForUpdates()

        if (savedInstanceState == null) trackNotificationOpen(intent)
        handleDeepLinkFromIntent(intent) { route ->
            deepLinkRouteState.value = route
        }

        setContent {
            var deepLinkRoute by deepLinkRouteState
            App(
                deepLinkRoute = deepLinkRoute,
                onDeepLinkConsume = { deepLinkRoute = null },
                onReady = { isAppReady = true }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let { newIntent ->
            setIntent(newIntent)
            trackNotificationOpen(newIntent)
            handleDeepLinkFromIntent(newIntent) { route ->
                lifecycleScope.launch {
                    deepLinkRouteState.value = route
                }
            }
        }
    }

    private fun trackNotificationOpen(intent: Intent?) {
        intent ?: return
        if (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY != 0) return
        val fromNotification = intent.getBooleanExtra(EXTRA_FROM_NOTIFICATION, false) ||
            intent.hasExtra("google.message_id")
        if (!fromNotification) return
        analyticsLogger.logEvent(
            eventName = EVENT_NOTIFICATION_OPENED,
            params = notificationOpenedParams(intent.getStringExtra("deep_link"))
        )
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
        ActivityContextHolder.set(this)
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

    override fun onDestroy() {
        super.onDestroy()
        ActivityContextHolder.clear()
    }

    companion object {
        const val EXTRA_FROM_NOTIFICATION = "from_notification"
    }
}
