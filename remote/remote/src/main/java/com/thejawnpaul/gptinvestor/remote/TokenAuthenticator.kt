package com.thejawnpaul.gptinvestor.remote

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val tokenApi: TokenApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        val refreshToken = tokenStorage.getRefreshToken() ?: return null

        val newTokenResponse = tokenApi.refreshToken("Bearer $refreshToken").execute()

        return if (newTokenResponse.isSuccessful) {
            val newAccessToken = newTokenResponse.body()?.accessToken ?: return null

            tokenStorage.saveAccessToken(newAccessToken)

            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        } else {

            null
        }
    }
}
