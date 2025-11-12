package com.thejawnpaul.gptinvestor.core.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<GPTInvestorDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(GPTInvestorDatabase.DB_NAME)

    return Room.databaseBuilder<GPTInvestorDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}