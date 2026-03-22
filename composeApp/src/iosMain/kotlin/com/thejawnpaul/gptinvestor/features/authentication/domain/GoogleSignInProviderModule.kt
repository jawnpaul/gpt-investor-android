package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.shared.BuildConfig
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * The OAuth2 web client ID from BuildConfig, exposed as a public top-level property so
 * that [SwiftGoogleSignInProvider] can read it from Swift via
 * `GoogleSignInProviderModuleKt.googleSignInWebClientId` (after `import ComposeApp`).
 *
 * This matches the pattern used by `mixpanelToken` in MixpanelProviderModule.
 */
val googleSignInWebClientId: String = BuildConfig.WEB_CLIENT_ID

/**
 * Returns a Koin module that overrides [NoOpGoogleSignInProvider] with the real
 * Swift-side implementation. Pass a [SwiftGoogleSignInProvider] instance from iosApp
 * and include the result in the `modules(...)` call inside `koinConfiguration<GPTKoinApp>`.
 */
fun googleSignInProviderModule(provider: GoogleSignInProvider): Module = module {
    single<GoogleSignInProvider> { provider }
}
