package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {
    // Signals
    @Query("SELECT * FROM signals ORDER BY timestamp DESC")
    fun getAllSignals(): Flow<List<SignalItem>>

    @Query("SELECT * FROM signals WHERE status = :status ORDER BY timestamp DESC")
    fun getSignalsByStatus(status: String): Flow<List<SignalItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: SignalItem): Long

    @Update
    suspend fun updateSignal(signal: SignalItem)

    @Query("DELETE FROM signals WHERE id = :id")
    suspend fun deleteSignalById(id: Int)

    @Query("DELETE FROM signals")
    suspend fun deleteAllSignals()

    // Historical Rounds
    @Query("SELECT * FROM historical_rounds ORDER BY timestamp DESC LIMIT 50")
    fun getRecentRounds(): Flow<List<HistoricalRound>>

    @Query("SELECT * FROM historical_rounds ORDER BY timestamp DESC")
    fun getAllRounds(): Flow<List<HistoricalRound>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: HistoricalRound): Long

    @Query("DELETE FROM historical_rounds WHERE id = :id")
    suspend fun deleteRoundById(id: Int)

    @Query("DELETE FROM historical_rounds")
    suspend fun deleteAllRounds()
}
