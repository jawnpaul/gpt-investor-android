package com.thejawnpaul.gptinvestor.core.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.thejawnpaul.gptinvestor.core.preferences.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun providesDataStore(): Module = module {
    single<DataStore<Preferences>> { createDataStore(androidContext()) }
}