package com.thejawnpaul.gptinvestor.remote

interface UnauthorizedCallback {
    fun onUnauthorized()

    suspend fun guestUniqueId(): String?
}
