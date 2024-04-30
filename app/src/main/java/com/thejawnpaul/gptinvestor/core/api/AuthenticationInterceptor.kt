package com.thejawnpaul.gptinvestor.core.api

import com.thejawnpaul.gptinvestor.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthenticationInterceptor :
    Interceptor {
    companion object {
        const val UNAUTHORIZED = 401
        const val TOKEN_TYPE = "Bearer "
        const val AUTH_HEADER = "Authorization"
        const val NO_AUTH_HEADER = "No Auth Header"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = BuildConfig.ACCESS_TOKEN

        val request = chain.request()

        return if (token != null) {
            // If token is not null, create authenticated request
            val interceptedRequest: Request = chain.createAuthenticatedRequest(token)
            chain.proceed(interceptedRequest)
        } else {
            chain.proceed(request)
        }
    }

    private fun Interceptor.Chain.createAuthenticatedRequest(token: String): Request {
        return request()
            .newBuilder()
            .header(AUTH_HEADER, TOKEN_TYPE + token)
            .build()
    }
}
