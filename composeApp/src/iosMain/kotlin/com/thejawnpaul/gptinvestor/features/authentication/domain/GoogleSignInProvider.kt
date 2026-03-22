package com.thejawnpaul.gptinvestor.features.authentication.domain

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
 * The default binding is [NoOpGoogleSignInProvider]. The iosApp overrides it at startup
 * via [googleSignInProviderModule], mirroring the MixpanelProvider bridge pattern.
 */
interface GoogleSignInProvider {
    /**
     * Triggers the Google Sign-In UI and returns the Google ID token via [onSuccess],
     * or an error message via [onError].
     */
    fun signIn(onSuccess: (idToken: String, accessToken: String) -> Unit, onError: (message: String) -> Unit)
}
