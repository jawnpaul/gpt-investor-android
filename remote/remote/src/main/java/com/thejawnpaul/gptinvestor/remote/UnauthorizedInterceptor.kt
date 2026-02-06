package com.thejawnpaul.gptinvestor.remote

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class UnauthorizedInterceptor @Inject constructor(
    private val unauthorizedCallback: UnauthorizedCallback
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 401) {
            unauthorizedCallback.onUnauthorized()
        }
        return response
    }
}
