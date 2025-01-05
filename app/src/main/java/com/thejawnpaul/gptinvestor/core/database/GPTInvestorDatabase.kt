package com.thejawnpaul.gptinvestor.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.company.data.local.model.CompanyEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationWithLastMessage
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.MessageEntity
import com.thejawnpaul.gptinvestor.features.toppick.data.local.dao.TopPickDao
import com.thejawnpaul.gptinvestor.features.toppick.data.local.model.TopPickEntity

@Database(
    entities = [CompanyEntity::class, ConversationEntity::class, MessageEntity::class, TopPickEntity::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2), AutoMigration(
            from = 2,
            to = 3
        ), AutoMigration(from = 3, to = 4)
    ],
    version = 4,
    exportSchema = true,
    views = [ConversationWithLastMessage::class]
)
@TypeConverters(Converters::class)
abstract class GPTInvestorDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "gptinvestor_database"
    }

    abstract fun companyDao(): CompanyDao

    abstract fun conversationDao(): ConversationDao

    abstract fun messageDao(): MessageDao

    abstract fun topPicksDao(): TopPickDao
}
