package com.thejawnpaul.gptinvestor.core.platform

import android.app.Activity
import java.lang.ref.WeakReference

object ActivityContextHolder {
    private var activityRef: WeakReference<Activity>? = null

    fun set(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    fun clear() {
        activityRef = null
    }

    fun get(): Activity? = activityRef?.get()
}
