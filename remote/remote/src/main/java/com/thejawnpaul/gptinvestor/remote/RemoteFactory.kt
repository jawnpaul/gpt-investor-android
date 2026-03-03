package com.thejawnpaul.gptinvestor.remote



/*
internal object RetrofitFactory {

    fun create(
        moshi: Moshi,
        unauthorizedInterceptor: UnauthorizedInterceptor,
        tokenStorage: TokenStorage,
        tokenAuthenticator: TokenAuthenticator
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .delegatingCallFactory { makeOkHttpClient(unauthorizedInterceptor, tokenStorage, tokenAuthenticator) }
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    private fun makeOkHttpClient(
        unauthorizedInterceptor: UnauthorizedInterceptor,
        tokenStorage: TokenStorage,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.HEADERS }
        } else {
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthenticationInterceptor(tokenStorage))
            .addInterceptor(unauthorizedInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Retrofit.Builder.delegatingCallFactory(
        delegate: dagger.Lazy<OkHttpClient>
    ): Retrofit.Builder = callFactory { delegate.get().newCall(it) }

    fun provideTokenApiService(moshi: Moshi): TokenApiService {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        } else {
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TokenApiService::class.java)
    }
}

private class AuthenticationInterceptor(private val tokenStorage: TokenStorage) : Interceptor {

    private val TOKEN_TYPE = "Bearer "
    private val AUTH_HEADER = "Authorization"

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStorage.getAccessToken()
        val request = chain.request()
        return if (token != null) {
            val interceptedRequest =
                chain.request().newBuilder().header(AUTH_HEADER, TOKEN_TYPE + token).build()
            chain.proceed(interceptedRequest)
        } else {
            chain.proceed(request)
        }
    }
}*/
