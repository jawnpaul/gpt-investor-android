package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider
import org.koin.core.annotation.Singleton

/**
 * Fallback no-op GoogleSignInProvider. Registered via @Singleton as a default.
 * The iosApp overrides it at startup via googleSignInProviderModule.
 */
@Singleton(binds = [GoogleSignInProvider::class])
class NoOpGoogleSignInProvider : GoogleSignInProvider {
    override fun signIn(onSuccess: (idToken: String, accessToken: String) -> Unit, onError: (message: String) -> Unit) {
        onError("GoogleSignInProvider not configured — pass a SwiftGoogleSignInProvider via googleSignInProviderModule")
    }
}
