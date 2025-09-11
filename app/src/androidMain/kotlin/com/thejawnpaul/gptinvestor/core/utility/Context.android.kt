package com.thejawnpaul.gptinvestor.core.utility

import android.content.Context
import java.lang.ref.WeakReference

actual object ActivityContext {
    private var contextRef: WeakReference<Context?>? = null

    fun set(context: Context?) {
        contextRef = WeakReference(context)
    }

    internal fun get(): Context? = contextRef?.get()
}

actual object AppContext {
    private var contextRef: WeakReference<Context?>? = null

    fun set(context: Any?) {
        contextRef = WeakReference(context as? Context)
    }

    internal fun get(): Any? = contextRef?.get()
}
