package com.thejawnpaul.gptinvestor.core.platform

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import org.koin.core.annotation.Singleton

@Singleton(binds = [NotificationPermissionController::class])
class AndroidNotificationPermissionController : NotificationPermissionController {
    @Composable
    override fun RequestPermissionIfNeeded(shouldRequest: Boolean, onGrant: () -> Unit, onDeny: () -> Unit) {
        if (!shouldRequest) return

        val currentOnGrant by rememberUpdatedState(onGrant)
        val currentOnDeny by rememberUpdatedState(onDeny)

        val context = LocalContext.current
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                currentOnGrant()
            } else {
                currentOnDeny()
            }
        }

        LaunchedEffect(shouldRequest) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        currentOnGrant()
                    }
                    else -> {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                currentOnGrant()
            }
        }
    }
}
