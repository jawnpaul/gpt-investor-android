package com.thejawnpaul.gptinvestor.core.di

import android.content.Context
import androidx.room.Room
import com.thejawnpaul.gptinvestor.core.database.GPTInvestorDatabase
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import com.thejawnpaul.gptinvestor.features.toppick.data.local.dao.TopPickDao
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
object DatabaseModule {

    @Singleton
    fun provideDataBase(context: Context): GPTInvestorDatabase = Room.databaseBuilder(
        context.applicationContext,
        GPTInvestorDatabase::class.java,
        GPTInvestorDatabase.DB_NAME
        // TODO:Remove fallback to destructive migration
    ).fallbackToDestructiveMigration()
        .build()

    @Singleton
    fun providesCompanyDao(db: GPTInvestorDatabase): CompanyDao = db.companyDao()

    @Singleton
    fun providesConversationDao(db: GPTInvestorDatabase): ConversationDao = db.conversationDao()

    @Singleton
    fun providesMessageDao(db: GPTInvestorDatabase): MessageDao = db.messageDao()

    @Singleton
    fun providesTopPicksDao(db: GPTInvestorDatabase): TopPickDao = db.topPicksDao()
}
