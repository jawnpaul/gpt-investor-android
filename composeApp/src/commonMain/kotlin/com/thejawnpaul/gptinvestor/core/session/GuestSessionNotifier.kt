package com.thejawnpaul.gptinvestor.core.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Singleton

enum class GuestSessionReason { Expired, Limit }
enum class GuestSessionSource { Request, Launch, Stream }

data class GuestSessionEvent(val reason: GuestSessionReason, val source: GuestSessionSource, val ticker: String? = null)

@Singleton
class GuestSessionNotifier {
    private val _signal = MutableSharedFlow<GuestSessionEvent>(extraBufferCapacity = 1)
    val signal: SharedFlow<GuestSessionEvent> = _signal.asSharedFlow()

    fun notify(event: GuestSessionEvent) {
        _signal.tryEmit(event)
    }
}
