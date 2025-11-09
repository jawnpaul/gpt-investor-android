package com.thejawnpaul.gptinvestor.core.di

import androidx.room.Room
import com.thejawnpaul.gptinvestor.core.database.GPTInvestorDatabase
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import com.thejawnpaul.gptinvestor.features.toppick.data.local.dao.TopPickDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single<GPTInvestorDatabase> {
        // TODO:Remove fallback to destructive migration
        Room.databaseBuilder(
            androidContext(),
            GPTInvestorDatabase::class.java,
            GPTInvestorDatabase.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    single<CompanyDao> { get<GPTInvestorDatabase>().companyDao() }

    single<ConversationDao> { get<GPTInvestorDatabase>().conversationDao() }

    single<MessageDao> { get<GPTInvestorDatabase>().messageDao() }

    single<TopPickDao> { get<GPTInvestorDatabase>().topPicksDao() }
}
