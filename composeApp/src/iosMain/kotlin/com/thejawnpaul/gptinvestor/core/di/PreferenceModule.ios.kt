package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.preferences.createDataStore
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
actual object PreferenceModule {
    @Singleton
    fun provideDataStore() = createDataStore()
}
