package com.thejawnpaul.gptinvestor.core.di

import androidx.room.RoomDatabase
import com.thejawnpaul.gptinvestor.core.database.GPTInvestorDatabase
import com.thejawnpaul.gptinvestor.core.database.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun databaseBuilderModule(): Module = module {
    single<RoomDatabase.Builder<GPTInvestorDatabase>> {
        getDatabaseBuilder(get())
    }
}