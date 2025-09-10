package com.thejawnpaul.gptinvestor.core.firebase

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

actual class User internal constructor(private val android: FirebaseUser) {
    actual val uid: String
        get() = android.uid
    actual val email: String?
        get() = android.email
    actual val displayName: String?
        get() = android.displayName
    actual val providerId: String
        get() = android.providerId

    actual suspend fun delete() {
        android.delete().await()
    }
}

fun FirebaseUser.toUser(): User = User(this)