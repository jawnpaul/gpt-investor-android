package com.thejawnpaul.gptinvestor.core.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<GPTInvestorDatabase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    val dbFile = requireNotNull(documentDirectory?.path) + "/${GPTInvestorDatabase.DB_NAME}.db"
    return Room.databaseBuilder<GPTInvestorDatabase>(
        name = dbFile,
        factory = { GPTInvestorDatabaseConstructor.initialize() }
    ).setDriver(BundledSQLiteDriver())
}
