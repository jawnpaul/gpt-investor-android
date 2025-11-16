package com.thejawnpaul.gptinvestor.core.firebase.auth

import com.google.firebase.auth.FirebaseAuth

actual class User {
    private val firebaseUser = FirebaseAuth.getInstance().currentUser ?: throw Exception("User not found")

    actual val uid: String
        get() = firebaseUser.uid
    actual val email: String?
        get() = firebaseUser.email
    actual val displayName: String?
        get() = firebaseUser.displayName
    actual val providerId: String
        get() = firebaseUser.providerId

    actual suspend fun delete() {
        firebaseUser.delete()
    }
}