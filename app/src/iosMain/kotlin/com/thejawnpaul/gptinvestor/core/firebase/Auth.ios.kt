package com.thejawnpaul.gptinvestor.core.firebase

import cocoapods.FirebaseAuth.FIRAuth
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalForeignApi::class)
actual class FirebaseAuthentication(private val auth: FIRAuth) {
    actual suspend fun signOut() {
        auth.signOut(null)

    }

    actual fun getAuthenticationState(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    actual val currentUser: User?
        get() = auth.currentUser()?.toUser()

    actual suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<User?> {
        TODO("Not yet implemented")
    }

    actual suspend fun signInWithCredentials(): Result<User?> {
        TODO("Not yet implemented")
    }

    actual suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<User?> {
        TODO("Not yet implemented")
    }
}