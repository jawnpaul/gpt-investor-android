package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.remote.UnauthorizedCallback
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Singleton
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Singleton(binds = [UnauthorizedCallback::class])
class UnauthorizedCallbackImpl :
    UnauthorizedCallback,
    KoinComponent {
    private val authenticationRepository: AuthenticationRepository by inject()

    override fun onUnauthorized() {
        runBlocking {
            authenticationRepository.signOut()
        }
    }
}
