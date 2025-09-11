package com.thejawnpaul.gptinvestor.core.firebase

interface IUser {
    val uid: String
    val email: String?
    val displayName: String?
    val providerId: String
    suspend fun delete()
}
