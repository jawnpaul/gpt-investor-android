package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.core.preferences.AppPreferences
import com.thejawnpaul.gptinvestor.core.session.GuestSessionEvent
import com.thejawnpaul.gptinvestor.core.session.GuestSessionNotifier
import com.thejawnpaul.gptinvestor.core.session.GuestSessionReason
import com.thejawnpaul.gptinvestor.core.session.GuestSessionSource
import com.thejawnpaul.gptinvestor.remote.UnauthorizedCallback
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Singleton
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Singleton(binds = [UnauthorizedCallback::class])
class UnauthorizedCallbackImpl :
    UnauthorizedCallback,
    KoinComponent {
    private val authenticationRepository: AuthenticationRepository by inject()
    private val appPreferences: AppPreferences by inject()
    private val guestSessionNotifier: GuestSessionNotifier by inject()

    override fun onUnauthorized() {
        runBlocking {
            val isGuest = appPreferences.isGuestLoggedIn.first() == true
            if (isGuest) {
                guestSessionNotifier.notify(
                    GuestSessionEvent(
                        reason = GuestSessionReason.Expired,
                        source = GuestSessionSource.Request
                    )
                )
            } else {
                authenticationRepository.signOut()
            }
        }
    }
}
