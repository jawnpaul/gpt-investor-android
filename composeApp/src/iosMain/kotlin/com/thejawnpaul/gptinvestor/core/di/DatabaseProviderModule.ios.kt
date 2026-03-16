package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.database.GPTInvestorDatabase
import com.thejawnpaul.gptinvestor.core.database.getDatabaseBuilder
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
actual class DatabaseProviderModule {
    @Singleton
    fun provideDatabase(): GPTInvestorDatabase = getDatabaseBuilder(null)
        .fallbackToDestructiveMigration(dropAllTables = false)
        .build()
}
