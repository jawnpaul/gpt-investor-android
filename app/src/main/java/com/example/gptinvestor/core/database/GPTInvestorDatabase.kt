package com.example.gptinvestor.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gptinvestor.features.company.data.local.dao.CompanyDao
import com.example.gptinvestor.features.company.data.local.model.CompanyEntity

@Database(
    entities = [CompanyEntity::class],
    version = 1,
    exportSchema = true
)
abstract class GPTInvestorDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "gptinvestor_database"
    }

    abstract fun companyDao(): CompanyDao
}
