package com.thejawnpaul.gptinvestor.remote

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    @Provides @Singleton fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides @Singleton fun provideRetrofit(
        moshi: Moshi,
        unauthorizedInterceptor: UnauthorizedInterceptor,
        tokenStorage: TokenStorage,
        tokenAuthenticator: TokenAuthenticator
    ): Retrofit = RetrofitFactory.create(moshi, unauthorizedInterceptor, tokenStorage, tokenAuthenticator)

    @Provides @Singleton fun provideTokenApiService(moshi: Moshi): TokenApiService =
        RetrofitFactory.provideTokenApiService(moshi)

}