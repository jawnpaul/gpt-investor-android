package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.User
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val currentUser: User?
    suspend fun signOut(): Result<Unit>
    fun getAuthState(): Flow<Boolean>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<String>
    suspend fun signUpWithEmailAndPassword(email: String, password: String, name: String): Result<String>
    suspend fun loginWithGoogle(platformContext: PlatformContext): Result<Unit>
    suspend fun loginWithApple(): Result<Unit>
    suspend fun signUpWithGoogle(platformContext: PlatformContext): Result<Unit>

    suspend fun signUpWithApple(): Result<Unit>
    suspend fun guestLogin(): Result<String>
}
