package com.thejawnpaul.gptinvestor.core.firebase.auth

import cocoapods.FirebaseAuth.FIRAuth
import kotlinx.cinterop.ExperimentalForeignApi
@OptIn(ExperimentalForeignApi::class)
actual class User {
    val firUser = FIRAuth.auth().currentUser() ?: throw Exception("User not found")
    actual val uid: String
        get() = firUser.uid()
    actual val email: String?
        get() = firUser.email()
    actual val displayName: String?
        get() = firUser.displayName()
    actual val providerId: String
        get() = firUser.providerID()

    actual suspend fun delete() {
        firUser.deleteWithCompletion(null)
    }
}