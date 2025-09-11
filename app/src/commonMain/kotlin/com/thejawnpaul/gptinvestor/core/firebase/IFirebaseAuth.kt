package com.thejawnpaul.gptinvestor.core.firebase

import kotlinx.coroutines.flow.Flow

interface IFirebaseAuth {
    suspend fun signOut()
    fun getAuthenticationState(): Flow<Boolean>
    val currentUser: IUser?

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<IUser?>
    suspend fun signInWithCredentials(): Result<IUser?>
    suspend fun  signInWithEmailAndPassword(email: String, password: String): Result<IUser?>
}

