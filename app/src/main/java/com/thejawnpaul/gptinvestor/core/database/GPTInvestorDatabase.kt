package com.thejawnpaul.gptinvestor.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thejawnpaul.gptinvestor.features.company.data.local.dao.CompanyDao
import com.thejawnpaul.gptinvestor.features.company.data.local.model.CompanyEntity

@Database(
    entities = [CompanyEntity::class],
    autoMigrations = [AutoMigration(from = 1, to = 2)],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class GPTInvestorDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "gptinvestor_database"
    }

    abstract fun companyDao(): CompanyDao
}
