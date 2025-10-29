package com.example.trackme.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Query("SELECT * FROM runs_table ORDER BY startTimeMillis DESC")
    fun getAllRunsSortedByDate(): Flow<List<RunEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RunEntity)

    @Delete
    suspend fun deleteRun(run: RunEntity)
}