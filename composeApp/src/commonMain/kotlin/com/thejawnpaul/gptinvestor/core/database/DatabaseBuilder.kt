package com.thejawnpaul.gptinvestor.core.database

import androidx.room.RoomDatabase

expect fun getDatabaseBuilder(context: Any? = null): RoomDatabase.Builder<GPTInvestorDatabase>
