package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SimulationLogDao {
    @Query("SELECT * FROM simulation_logs ORDER BY timestampMillis DESC")
    fun getAllLogs(): Flow<List<SimulationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SimulationLog): Long
}
