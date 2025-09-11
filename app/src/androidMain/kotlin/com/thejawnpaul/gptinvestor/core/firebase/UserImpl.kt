package com.thejawnpaul.gptinvestor.core.firebase

import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class UserImpl(private val android: FirebaseUser) : IUser {
    override val uid: String
        get() = android.uid
    override val email: String?
        get() = android.email
    override val displayName: String?
        get() = android.displayName
    override val providerId: String
        get() = android.providerId

    override suspend fun delete() {
        android.delete().await()
    }
}

fun FirebaseUser.toUser(): IUser = UserImpl(this)
