package com.thejawnpaul.gptinvestor.core.utility

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SwiftFlow<T> {
    private var emitter: ((T) -> Unit)? = null
    private var onCompletion: (() -> Unit)? = null
    private var onError: ((Throwable) -> Unit)? = null

    fun asFlow(callback: (() -> Unit)? = null): Flow<T> = callbackFlow {
        emitter = { value -> trySend(value).isSuccess }
        onCompletion = { close() }
        onError = {  close(it) }
        awaitClose {
            callback?.invoke()
            emitter = null
            onCompletion = null
            onError = null
        }
    }

    fun emit(value: T) {
        emitter?.invoke(value)
    }

    fun complete() {
        onCompletion?.invoke()
    }

    fun error(throwable: Throwable) {
        onError?.invoke(throwable)
    }
}