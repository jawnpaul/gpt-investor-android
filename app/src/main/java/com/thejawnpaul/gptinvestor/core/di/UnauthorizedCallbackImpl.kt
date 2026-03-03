package com.thejawnpaul.gptinvestor.core.di

import android.content.Context
import com.thejawnpaul.gptinvestor.features.authentication.domain.AuthenticationRepository
import com.thejawnpaul.gptinvestor.remote.UnauthorizedCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Provider
import kotlinx.coroutines.runBlocking

class UnauthorizedCallbackImpl @Inject constructor(
    private val authenticationRepository: Provider<AuthenticationRepository>,
    @ApplicationContext private val context: Context
) : UnauthorizedCallback {
    override fun onUnauthorized() {
        runBlocking {
            authenticationRepository.get().signOut(context)
        }
    }
}
