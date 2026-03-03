package com.thejawnpaul.gptinvestor.core.preferences

import com.thejawnpaul.gptinvestor.remote.TokenStorage
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Singleton
class TokenStorageImpl @Inject constructor(
    private val gptInvestorPreferences: GPTInvestorPreferences
) : TokenStorage {
    override fun getAccessToken(): String? {
        return runBlocking {
            gptInvestorPreferences.accessToken.first()
        }
    }

    override fun getRefreshToken(): String? {
        return runBlocking {
            gptInvestorPreferences.refreshToken.first()
        }
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
