package com.thejawnpaul.gptinvestor.core.di

import android.content.Context
import androidx.room.Room
import com.thejawnpaul.gptinvestor.core.database.GPTInvestorDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    companion object {
        @Singleton
        @Provides
        fun provideDataBase(@ApplicationContext context: Context): GPTInvestorDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                GPTInvestorDatabase::class.java,
                GPTInvestorDatabase.DB_NAME
            ).fallbackToDestructiveMigration()
                .build()
        }

        // TODO:Remove fallback to destructive migration

        @Singleton
        @Provides
        fun providesCompanyDao(db: GPTInvestorDatabase) = db.companyDao()
    }
}
