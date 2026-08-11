package com.example.data.repository

import com.example.data.ai.AviatorPredictorEngine
import com.example.data.db.HistoricalRound
import com.example.data.db.SignalDao
import com.example.data.db.SignalItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AviatorRepository(private val signalDao: SignalDao) {

    val allSignals: Flow<List<SignalItem>> = signalDao.getAllSignals()
    val recentRounds: Flow<List<HistoricalRound>> = signalDao.getRecentRounds()

    fun getSignalsByStatus(status: String): Flow<List<SignalItem>> =
        signalDao.getSignalsByStatus(status)

    suspend fun addHistoricalRound(multiplier: Double, loggedBy: String = "Team Member") {
        val round = HistoricalRound(
            multiplier = multiplier,
            loggedBy = loggedBy
        )
        signalDao.insertRound(round)

        // Check if there are PENDING signals and evaluate them with this actual outcome!
        evaluatePendingSignals(multiplier)
    }

    private suspend fun evaluatePendingSignals(actualMultiplier: Double) {
        val currentSignals = signalDao.getAllSignals().first()
        val pendingSignals = currentSignals.filter { it.status == "PENDING" }

        for (signal in pendingSignals) {
            // A signal is considered a "HIT" if the actual multiplier reached or exceeded the recommended cashout target!
            val isHit = actualMultiplier >= signal.recommendedCashout
            val newStatus = if (isHit) "HIT" else "MISS"

            val updated = signal.copy(
                actualMultiplier = actualMultiplier,
                status = newStatus
            )
            signalDao.updateSignal(updated)
        }
    }

    suspend fun generateNewSignal(teamMember: String, useAiDeepScan: Boolean = true): SignalItem {
        val rounds = signalDao.getRecentRounds().first().map { it.multiplier }
        val signal = AviatorPredictorEngine.generateSignal(rounds, teamMember, useAiDeepScan)
        val id = signalDao.insertSignal(signal)
        return signal.copy(id = id.toInt())
    }

    suspend fun updateSignalOutcome(signalId: Int, actualMultiplier: Double) {
        val signals = signalDao.getAllSignals().first()
        val existing = signals.find { it.id == signalId } ?: return

        val isHit = actualMultiplier >= existing.recommendedCashout
        val updated = existing.copy(
            actualMultiplier = actualMultiplier,
            status = if (isHit) "HIT" else "MISS"
        )
        signalDao.updateSignal(updated)
    }

    suspend fun deleteSignal(id: Int) {
        signalDao.deleteSignalById(id)
    }

    suspend fun deleteRound(id: Int) {
        signalDao.deleteRoundById(id)
    }

    suspend fun clearAllData() {
        signalDao.deleteAllSignals()
        signalDao.deleteAllRounds()
    }

    suspend fun seedInitialDataIfEmpty() {
        val existingRounds = signalDao.getRecentRounds().first()
        if (existingRounds.isEmpty()) {
            val sampleRounds = listOf(
                1.15, 2.30, 1.08, 3.45, 1.25, 15.20, 1.10, 2.10, 1.85, 4.10,
                1.05, 1.30, 2.80, 1.12, 6.50, 1.90, 2.05, 1.18, 3.20, 1.02
            )
            sampleRounds.forEach { mult ->
                signalDao.insertRound(HistoricalRound(multiplier = mult, loggedBy = "Alpha Squad"))
            }

            // Seed sample past signals
            val sampleSignals = listOf(
                SignalItem(
                    predictedMultiplier = 2.40,
                    minTargetMultiplier = 1.80,
                    maxTargetMultiplier = 3.10,
                    recommendedCashout = 2.00,
                    riskLevel = "SAFE",
                    confidenceScore = 92,
                    actualMultiplier = 2.30,
                    status = "HIT",
                    patternDetected = "Low-Spike Cluster Rebound",
                    teamMember = "Capt. Alex",
                    notes = "Target hit cleanly at 2.30x before round crash."
                ),
                SignalItem(
                    predictedMultiplier = 5.20,
                    minTargetMultiplier = 3.50,
                    maxTargetMultiplier = 7.00,
                    recommendedCashout = 3.80,
                    riskLevel = "MOONSHOT",
                    confidenceScore = 78,
                    actualMultiplier = 6.50,
                    status = "HIT",
                    patternDetected = "Momentum Expansion Wave",
                    teamMember = "Vip Squad",
                    notes = "High-X wave captured up to 6.50x."
                ),
                SignalItem(
                    predictedMultiplier = 3.10,
                    minTargetMultiplier = 2.20,
                    maxTargetMultiplier = 4.00,
                    recommendedCashout = 2.50,
                    riskLevel = "MODERATE",
                    confidenceScore = 84,
                    actualMultiplier = 1.12,
                    status = "MISS",
                    patternDetected = "Early Step Reversion",
                    teamMember = "Beta Ops",
                    notes = "Unexpected instant crash at 1.12x."
                ),
                SignalItem(
                    predictedMultiplier = 1.85,
                    minTargetMultiplier = 1.40,
                    maxTargetMultiplier = 2.30,
                    recommendedCashout = 1.65,
                    riskLevel = "SAFE",
                    confidenceScore = 94,
                    actualMultiplier = 2.05,
                    status = "HIT",
                    patternDetected = "Standard Delta Safety",
                    teamMember = "Capt. Alex",
                    notes = "Safe target met with 94% confidence."
                )
            )
            sampleSignals.forEach { signalDao.insertSignal(it) }
        }
    }
}
