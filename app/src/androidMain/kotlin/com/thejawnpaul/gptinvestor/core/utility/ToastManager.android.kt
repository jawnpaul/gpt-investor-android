package com.thejawnpaul.gptinvestor.core.utility

import android.app.Activity
import android.widget.Toast

var activityProvider: () -> Activity? = { null }

fun setActivityProvider(provider: () -> Activity?) {
    activityProvider = provider
}

actual open class ToastManager actual constructor() {
    actual fun showToast(
        message: String,
        duration: ToastDuration
    ) {
        val context = activityProvider.invoke() ?: return
        val time = when (duration) {
            ToastDuration.Short -> Toast.LENGTH_SHORT
            ToastDuration.Long -> Toast.LENGTH_LONG
        }
        Toast.makeText(context, message, time).show()
    }
}