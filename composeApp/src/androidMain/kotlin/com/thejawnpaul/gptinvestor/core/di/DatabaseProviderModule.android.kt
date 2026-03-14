package com.thejawnpaul.gptinvestor.core.di

import android.content.Context
import com.thejawnpaul.gptinvestor.core.database.getDatabaseBuilder
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
actual class DatabaseProviderModule {
    @Singleton
    fun providesDatabase(context: Context) = getDatabaseBuilder(context)
        .fallbackToDestructiveMigration(dropAllTables = false)
        .build()
}
