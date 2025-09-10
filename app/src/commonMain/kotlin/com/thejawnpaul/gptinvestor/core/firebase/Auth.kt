package com.thejawnpaul.gptinvestor.core.firebase

import kotlinx.coroutines.flow.Flow

expect class FirebaseAuthentication {
    suspend fun signOut()
    fun getAuthenticationState(): Flow<Boolean>
    val currentUser: User?

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<User?>
    suspend fun signInWithCredentials(): Result<User?>
    suspend fun  signInWithEmailAndPassword(email: String, password: String): Result<User?>
}

