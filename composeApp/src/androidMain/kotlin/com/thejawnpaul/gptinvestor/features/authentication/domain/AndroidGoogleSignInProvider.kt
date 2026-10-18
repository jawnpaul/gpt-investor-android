package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.core.platform.GoogleSignInProvider
import org.koin.core.annotation.Singleton

/**
 * Android implementation of [GoogleSignInProvider].
 * Since Android uses Credential Manager directly in AuthenticationRepository.android.kt,
 * this remains a no-op but fulfills the Koin dependency requirement.
 */
@Singleton(binds = [GoogleSignInProvider::class])
class AndroidGoogleSignInProvider : NoOpGoogleSignInProvider()
