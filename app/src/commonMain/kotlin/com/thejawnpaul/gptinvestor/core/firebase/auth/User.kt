package com.thejawnpaul.gptinvestor.core.firebase.auth

expect class User {
    val uid: String
    val email: String?
    val displayName: String?
    val providerId: String
    suspend fun delete()
}
