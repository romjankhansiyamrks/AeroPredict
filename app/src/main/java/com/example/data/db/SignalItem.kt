package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signals")
data class SignalItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val predictedMultiplier: Double,
    val minTargetMultiplier: Double,
    val maxTargetMultiplier: Double,
    val recommendedCashout: Double,
    val riskLevel: String, // "SAFE", "MODERATE", "MOONSHOT"
    val confidenceScore: Int, // 0 to 100
    val actualMultiplier: Double? = null,
    val status: String = "PENDING", // "HIT", "MISS", "PENDING"
    val patternDetected: String,
    val teamMember: String = "Alpha Flight",
    val aiModelUsed: String = "Aviator-AI Engine v3.5",
    val notes: String = ""
)
