package com.thejawnpaul.gptinvestor.core.preferences

import com.thejawnpaul.gptinvestor.remote.TokenStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Singleton

@Singleton(binds = [TokenStorage::class])
class TokenStorageImpl(private val gptInvestorPreferences: AppPreferences) : TokenStorage {
    override fun getAccessToken(): String? = runBlocking {
        gptInvestorPreferences.accessToken.first()
    }

    override fun getRefreshToken(): String? = runBlocking {
        gptInvestorPreferences.refreshToken.first()
    }

    override fun saveAccessToken(token: String) {
        runBlocking {
            gptInvestorPreferences.setAccessToken(token)
        }
    }

    override fun saveRefreshToken(token: String) {
        runBlocking {
            gptInvestorPreferences.setRefreshToken(token)
        }
    }
}
