package com.thejawnpaul.gptinvestor.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thejawnpaul.gptinvestor.core.preferences.createDataStore
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
object PreferenceModule {
    @Singleton
    fun provideDataStore(): DataStore<Preferences> = createDataStore()
}
