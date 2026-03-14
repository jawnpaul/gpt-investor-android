package com.thejawnpaul.gptinvestor.features.authentication.domain

import com.thejawnpaul.gptinvestor.core.platform.PlatformContext
import com.thejawnpaul.gptinvestor.features.authentication.data.remote.User
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    val currentUser: User?
    suspend fun signOut()
    fun getAuthState(): Flow<Boolean>
    suspend fun deleteAccount()
    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<String>
    suspend fun signUpWithEmailAndPassword(email: String, password: String, name: String): Result<String>
    suspend fun loginWithGoogle(platformContext: PlatformContext): Result<Unit>
}
