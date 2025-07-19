package com.thejawnpaul.gptinvestor.core.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.company.data.local.model.CompanyEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.ConversationDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.dao.MessageDao
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationEntity
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.ConversationWithLastMessage
import com.thejawnpaul.gptinvestor.features.conversation.data.local.model.MessageEntity
import com.thejawnpaul.gptinvestor.features.toppick.data.local.dao.TopPickDao
import com.thejawnpaul.gptinvestor.features.toppick.data.local.model.TopPickEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [CompanyEntity::class, ConversationEntity::class, MessageEntity::class,
        TopPickEntity::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7)
    ],
    version = 7,
    exportSchema = true,
    views = [ConversationWithLastMessage::class]
)
@TypeConverters(Converters::class)
@ConstructedBy(GPTInvestorDatabaseConstructor::class)
abstract class GPTInvestorDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "gptinvestor_database.db"
    }

    abstract fun companyDao(): CompanyDao

    abstract fun conversationDao(): ConversationDao

    abstract fun messageDao(): MessageDao

    abstract fun topPicksDao(): TopPickDao
}

@Suppress("KotlinNoActualForExpect", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object GPTInvestorDatabaseConstructor: RoomDatabaseConstructor<GPTInvestorDatabase> {
    override fun initialize(): GPTInvestorDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<GPTInvestorDatabase>
): GPTInvestorDatabase {
    return builder
        .addMigrations()
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

fun getCompanyDao(db: GPTInvestorDatabase): CompanyDao = db.companyDao()

fun getConversationDao(db: GPTInvestorDatabase): ConversationDao = db.conversationDao()

fun getMessageDao(db: GPTInvestorDatabase): MessageDao = db.messageDao()

fun getTopPicksDao(db: GPTInvestorDatabase): TopPickDao = db.topPicksDao()