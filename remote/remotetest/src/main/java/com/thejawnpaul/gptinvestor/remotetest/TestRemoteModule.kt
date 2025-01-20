package com.thejawnpaul.gptinvestor.remotetest

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class TestRemoteModule {
  @Provides
  @Singleton
  fun provideRetrofit(moshi: Moshi): Retrofit =
    Retrofit.Builder()
      .baseUrl("url")
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()

  @Provides @Singleton fun provideMoshi(): Moshi = Moshi.Builder().build()
}
