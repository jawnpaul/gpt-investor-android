package com.thejawnpaul.gptinvestor.features.authentication.domain

import org.koin.core.annotation.Singleton

@Singleton(binds = [GoogleSignInProvider::class])
class NoOpGoogleSignInProvider : GoogleSignInProvider {
    override fun signIn(onSuccess: (idToken: String, accessToken: String) -> Unit, onError: (message: String) -> Unit) {
        onError("GoogleSignInProvider not configured — pass a SwiftGoogleSignInProvider via googleSignInProviderModule")
    }
}
