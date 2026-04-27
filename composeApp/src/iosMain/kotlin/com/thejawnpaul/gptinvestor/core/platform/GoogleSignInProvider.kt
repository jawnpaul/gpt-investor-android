package com.thejawnpaul.gptinvestor.core.platform

/**
 * ObjC-visible protocol bridging Google Sign-In to Swift.
 *
 * The Google Sign-In iOS SDK presents a sign-in sheet from a UIViewController, which
 * requires UIKit access only available from Swift. This interface is exposed as an ObjC
 * protocol in the ComposeApp framework, allowing Swift to implement it using the
 * GoogleSignIn-iOS SPM package.
 *
 * Both callbacks use [String] to keep the ObjC bridge clean (avoids KotlinThrowable).
 *
 * The default binding is [com.thejawnpaul.gptinvestor.features.authentication.domain.NoOpGoogleSignInProvider]. The iosApp overrides it at startup
 * via [com.thejawnpaul.gptinvestor.features.authentication.domain.googleSignInProviderModule], mirroring the MixpanelProvider bridge pattern.
 */
interface GoogleSignInProvider {
    fun signIn(onSuccess: (idToken: String, accessToken: String) -> Unit, onError: (message: String) -> Unit)
}
