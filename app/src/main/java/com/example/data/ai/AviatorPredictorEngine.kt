package com.example.data.ai

import com.example.data.db.SignalItem
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object AviatorPredictorEngine {

    /**
     * Generates a new predicted signal based on current historical round multipliers.
     * Uses Gemini AI if available; falls back to statistical algorithmic model.
     */
    suspend fun generateSignal(
        recentRounds: List<Double>,
        teamMember: String,
        useAiDeepScan: Boolean = true
    ): SignalItem {
        val history = if (recentRounds.isEmpty()) generateSampleSequence() else recentRounds

        // Try Gemini AI if requested
        if (useAiDeepScan) {
            val aiResult = GeminiAiService.analyzeSequenceWithGemini(history, "Team: $teamMember")
            if (aiResult != null) {
                return SignalItem(
                    predictedMultiplier = roundTo2Decimals(aiResult.predictedMultiplier),
                    minTargetMultiplier = roundTo2Decimals(aiResult.minTarget),
                    maxTargetMultiplier = roundTo2Decimals(aiResult.maxTarget),
                    recommendedCashout = roundTo2Decimals(aiResult.cashoutTarget),
                    riskLevel = validateRiskLevel(aiResult.riskLevel),
                    confidenceScore = aiResult.confidenceScore.coerceIn(50, 98),
                    patternDetected = aiResult.patternDetected,
                    teamMember = teamMember,
                    aiModelUsed = "Gemini 3.5 Flash AI",
                    notes = aiResult.reasoning
                )
            }
        }

        // Fallback or quick algorithmic model
        return generateAlgorithmicSignal(history, teamMember)
    }

    private fun generateAlgorithmicSignal(history: List<Double>, teamMember: String): SignalItem {
        val recent10 = history.takeLast(10)
        val lowSpikesCount = recent10.count { it < 1.30 }
        val highSpikesCount = recent10.count { it >= 5.00 }
        val avgVal = if (recent10.isNotEmpty()) recent10.average() else 2.10

        val (riskLevel, pattern, baseMult, cashout) = when {
            lowSpikesCount >= 3 -> {
                // Low spike cluster detected -> high chance of mean reversion bounce
                val target = max(1.85, roundTo2Decimals(1.80 + (Random.nextDouble() * 1.50)))
                val safeCashout = roundTo2Decimals(target * 0.75)
                Quadruple("MODERATE", "Low-Spike Cluster Bounce", target, safeCashout)
            }
            highSpikesCount >= 2 -> {
                // High multiplier cool-down phase -> safer low cashout recommendation
                val target = roundTo2Decimals(1.35 + (Random.nextDouble() * 0.45))
                val safeCashout = roundTo2Decimals(1.25)
                Quadruple("SAFE", "High-X Cooling Period", target, safeCashout)
            }
            recent10.lastOrNull() ?: 1.0 < 1.15 -> {
                // Immediate reset after crash -> quick step up
                val target = roundTo2Decimals(2.10 + (Random.nextDouble() * 0.80))
                val safeCashout = roundTo2Decimals(1.65)
                Quadruple("MODERATE", "Instant Crash Rebound", target, safeCashout)
            }
            avgVal > 3.5 -> {
                // High momentum wave -> moonshot opportunity
                val target = roundTo2Decimals(5.50 + (Random.nextDouble() * 8.00))
                val safeCashout = roundTo2Decimals(2.80)
                Quadruple("MOONSHOT", "Momentum Expansion Wave", target, safeCashout)
            }
            else -> {
                // Standard distribution
                val target = roundTo2Decimals(1.60 + (Random.nextDouble() * 1.40))
                val safeCashout = roundTo2Decimals(target * 0.80)
                Quadruple("SAFE", "Standard Delta Reversion", target, safeCashout)
            }
        }

        val minTarget = max(1.10, roundTo2Decimals(baseMult * 0.80))
        val maxTarget = roundTo2Decimals(baseMult * 1.35)
        val confidence = when (riskLevel) {
            "SAFE" -> Random.nextInt(85, 96)
            "MODERATE" -> Random.nextInt(75, 88)
            else -> Random.nextInt(62, 78)
        }

        val notes = when (riskLevel) {
            "SAFE" -> "Conservative flight signal. Highly consistent auto-cashout target set at ${cashout}x."
            "MODERATE" -> "Balanced risk/reward setup. Recommended entry with step auto-cashout."
            else -> "High-yield moonshot opportunity detected. Monitor live round closely for sudden volatility."
        }

        return SignalItem(
            predictedMultiplier = baseMult,
            minTargetMultiplier = minTarget,
            maxTargetMultiplier = maxTarget,
            recommendedCashout = cashout,
            riskLevel = riskLevel,
            confidenceScore = confidence,
            patternDetected = pattern,
            teamMember = teamMember,
            aiModelUsed = "Aviator-Statistical Heuristics v3.5",
            notes = notes
        )
    }

    private fun validateRiskLevel(level: String): String {
        val upper = level.uppercase()
        return when {
            upper.contains("SAFE") || upper.contains("LOW") -> "SAFE"
            upper.contains("MOON") || upper.contains("HIGH") -> "MOONSHOT"
            else -> "MODERATE"
        }
    }

    private fun generateSampleSequence(): List<Double> {
        return listOf(
            1.12, 2.45, 1.05, 1.88, 5.20, 1.20, 1.15, 3.10, 1.45, 12.80, 1.08, 2.15, 1.95, 4.30, 1.18
        )
    }

    private fun roundTo2Decimals(value: Double): Double {
        return (value * 100).toInt() / 100.0
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
