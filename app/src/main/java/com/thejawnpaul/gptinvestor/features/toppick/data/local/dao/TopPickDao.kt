package com.thejawnpaul.gptinvestor.features.toppick.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.thejawnpaul.gptinvestor.features.toppick.data.local.model.TopPickEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopPickDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTopPicks(list: List<TopPickEntity>)

    @Query("SELECT * FROM top_picks_table ORDER BY id")
    suspend fun getAllTopPicks(): List<TopPickEntity>

    @Query("DELETE FROM top_picks_table WHERE isSaved = 0")
    suspend fun deleteUnsavedTopPicks()

    @Query("SELECT * FROM top_picks_table WHERE id =:id")
    suspend fun getSingleTopPick(id: String): TopPickEntity

    @Update
    suspend fun updateTopPick(topPickEntity: TopPickEntity)

    @Query("SELECT * FROM top_picks_table WHERE isSaved = 1")
    suspend fun getSavedTopPicks(): List<TopPickEntity>

    @Query("SELECT * FROM top_picks_table WHERE date =:date ORDER BY confidenceScore DESC")
    fun getTopPicksFlow(date: String): Flow<List<TopPickEntity>>

    @Transaction
    suspend fun replaceUnsavedWithNewPicks(newPicks: List<TopPickEntity>) {
        deleteUnsavedTopPicks()
        insertTopPicks(newPicks)
    }
}
