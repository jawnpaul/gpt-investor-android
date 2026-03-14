package com.thejawnpaul.gptinvestor.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<GPTInvestorDatabase> {
    require(context is Context)
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(GPTInvestorDatabase.DB_NAME)
    return Room.databaseBuilder<GPTInvestorDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
