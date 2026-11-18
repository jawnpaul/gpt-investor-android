package com.thejawnpaul.gptinvestor.features.onboarding.domain

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.annotation.Singleton

@Singleton
class AskSession {
    private val mutex = Mutex()
    private var claimedBy: String? = null

    suspend fun claim(owner: String): Boolean = mutex.withLock {
        if (claimedBy == owner) return@withLock false
        claimedBy = owner
        true
    }
}
