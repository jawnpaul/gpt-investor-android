package com.thejawnpaul.gptinvestor.core.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Singleton

@Singleton
class GuestRateLimitNotifier {
    private val _signal = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val signal: SharedFlow<Unit> = _signal.asSharedFlow()

    fun notifyRateLimit() {
        _signal.tryEmit(Unit)
    }
}
