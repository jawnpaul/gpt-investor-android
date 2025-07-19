package com.thejawnpaul.gptinvestor.core.di

import com.thejawnpaul.gptinvestor.core.database.getCompanyDao
import com.thejawnpaul.gptinvestor.core.database.getConversationDao
import com.thejawnpaul.gptinvestor.core.database.getMessageDao
import com.thejawnpaul.gptinvestor.core.database.getRoomDatabase
import com.thejawnpaul.gptinvestor.core.database.getTopPicksDao
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import com.thejawnpaul.gptinvestor.features.toppick.data.local.dao.TopPickDao
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun databaseBuilderModule(): Module


val provideDatabaseModule: List<Module> = module {
    single { getRoomDatabase(get()) }
    single<CompanyDao> { getCompanyDao(get()) }
    single<ConversationDao> { getConversationDao(get()) }
    single<MessageDao> { getMessageDao(get()) }
    single<TopPickDao> { getTopPicksDao(get()) }
} + databaseBuilderModule()
