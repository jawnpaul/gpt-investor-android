package com.thejawnpaul.gptinvestor.remote

interface TokenStorage {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun saveAccessToken(token: String)
    fun saveRefreshToken(token: String)
}
