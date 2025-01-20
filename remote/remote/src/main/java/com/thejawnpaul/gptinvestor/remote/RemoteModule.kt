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

    @Provides @Singleton fun provideRetrofit(moshi: Moshi): Retrofit = RetrofitFactory.create(moshi)

}