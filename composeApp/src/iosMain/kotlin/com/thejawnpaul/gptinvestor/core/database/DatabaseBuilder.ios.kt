package com.thejawnpaul.gptinvestor.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<GPTInvestorDatabase> {
    val dbFile = NSHomeDirectory() + "/${GPTInvestorDatabase.DB_NAME}.db"
    return Room.databaseBuilder<GPTInvestorDatabase>(
        name = dbFile,
        factory = { GPTInvestorDatabaseConstructor.initialize() }
    ).setDriver(BundledSQLiteDriver())
}
