package com.thejawnpaul.gptinvestor.remote

import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal object RetrofitFactory {

    fun create(moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .delegatingCallFactory { makeOkHttpClient() }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    private fun makeOkHttpClient(): OkHttpClient {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        } else {
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthenticationInterceptor)
            .build()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Retrofit.Builder.delegatingCallFactory(
        delegate: dagger.Lazy<OkHttpClient>
    ): Retrofit.Builder = callFactory { delegate.get().newCall(it) }
}

private object AuthenticationInterceptor : Interceptor {

    private const val TOKEN_TYPE = "Bearer "
    private const val AUTH_HEADER = "Authorization"

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = BuildConfig.ACCESS_TOKEN.takeIf { it.isNotEmpty() }
        val request = chain.request()
        return if (token != null) {
            val interceptedRequest =
                chain.request().newBuilder().header(AUTH_HEADER, TOKEN_TYPE + token).build()
            chain.proceed(interceptedRequest)
        } else {
            chain.proceed(request)
        }
    }
}