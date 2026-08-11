package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historical_rounds")
data class HistoricalRound(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val multiplier: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val roundCode: String = "",
    val loggedBy: String = "Team Member"
)
