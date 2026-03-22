package com.thejawnpaul.gptinvestor.features.authentication.domain

class NoOpGoogleSignInProvider : GoogleSignInProvider {
    override fun signIn(onSuccess: (idToken: String, accessToken: String) -> Unit, onError: (message: String) -> Unit) {
        onError("GoogleSignInProvider not configured — pass a SwiftGoogleSignInProvider via googleSignInProviderModule")
    }
}
