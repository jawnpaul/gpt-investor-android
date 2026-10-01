package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider

/**
 * Fallback no-op GoogleSignInProvider.
 *
 * This class is NOT annotated with @Singleton to avoid it being automatically
 * registered by @ComponentScan on all platforms. On iOS, we provide a real
 * SwiftGoogleSignInProvider. On Android, we register a subclass in androidMain.
 */
open class NoOpGoogleSignInProvider : GoogleSignInProvider {
    override fun signIn(onSuccess: (idToken: String, accessToken: String) -> Unit, onError: (message: String) -> Unit) {
        onError("GoogleSignInProvider not configured — pass a SwiftGoogleSignInProvider via googleSignInProviderModule")
    }
}
