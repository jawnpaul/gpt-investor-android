package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider

/**
 * Fallback no-op GoogleSignInProvider. Not registered via @Singleton — the real
 * SwiftGoogleSignInProvider is always injected by mainViewController before Koin starts,
 * so @ComponentScan must not discover this class, or it will override the real provider.
 */
class NoOpGoogleSignInProvider : GoogleSignInProvider {
    override fun signIn(onSuccess: (idToken: String, accessToken: String) -> Unit, onError: (message: String) -> Unit) {
        onError("GoogleSignInProvider not configured — pass a SwiftGoogleSignInProvider via googleSignInProviderModule")
    }
}
