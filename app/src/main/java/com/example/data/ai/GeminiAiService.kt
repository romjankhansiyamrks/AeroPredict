package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GeminiAnalysisResult(
    val predictedMultiplier: Double,
    val minTarget: Double,
    val maxTarget: Double,
    val cashoutTarget: Double,
    val riskLevel: String, // "SAFE", "MODERATE", "MOONSHOT"
    val confidenceScore: Int, // 0-100
    val patternDetected: String,
    val reasoning: String
)

object GeminiAiService {
    private const val TAG = "GeminiAiService"
    private const val MODEL = "gemini-3.5-flash"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeSequenceWithGemini(
        recentMultipliers: List<Double>,
        teamContext: String
    ): GeminiAnalysisResult? = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid Gemini API key configured in BuildConfig. Using heuristic engine.")
            return@withContext null
        }

        val prompt = """
            You are an advanced AI crash-game signal predictor for Aviator multiplier sequences.
            Analyze the following recent historical multipliers sequence in chronological order (oldest to newest):
            ${recentMultipliers.takeLast(25).joinToString(", ") { "${it}x" }}

            Team Context: $teamContext

            Instructions:
            1. Identify statistical cluster patterns (e.g., low-multiplier spike sequence < 1.3x, mean reversion expectation, momentum wave, high x cool-down period).
            2. Predict the next target multiplier range and optimal auto-cashout target for safety.
            3. Assess risk level strictly as one of: "SAFE", "MODERATE", "MOONSHOT".
            4. Provide a confidence score integer from 50 to 98.
            5. Return output ONLY in raw valid JSON format matching this structure (no markdown fences or formatting code blocks):
            {
              "predictedMultiplier": 2.45,
              "minTarget": 1.80,
              "maxTarget": 3.20,
              "cashoutTarget": 2.10,
              "riskLevel": "MODERATE",
              "confidenceScore": 86,
              "patternDetected": "Low-Spike Rebound Cluster",
              "reasoning": "Sequence showed three consecutive low multiplier resets (<1.2x) triggering a high-probability mid-tier expansion wave."
            }
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
            })
        }.toString()

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent?key=$apiKey")
            .post(jsonBody.toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val responseText = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error: ${response.code} $responseText")
                return@withContext null
            }

            // Extract content text from JSON
            val rawText = parseTextFromResponse(responseText)
            if (rawText.isNullOrBlank()) return@withContext null

            // Clean markdown codeblocks if model included them
            val cleanJson = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            parseAnalysisJson(cleanJson)
        } catch (e: Exception) {
            Log.e(TAG, "Failed calling Gemini API", e)
            null
        }
    }

    private fun parseTextFromResponse(jsonResponse: String): String? {
        return try {
            val root = JSONObject(jsonResponse)
            val candidates = root.optJSONArray("candidates") ?: return null
            val firstCandidate = candidates.optJSONObject(0) ?: return null
            val content = firstCandidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            val firstPart = parts.optJSONObject(0) ?: return null
            firstPart.optString("text", null)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseAnalysisJson(jsonStr: String): GeminiAnalysisResult? {
        return try {
            val obj = JSONObject(jsonStr)
            val predicted = obj.optDouble("predictedMultiplier", 2.10)
            val minT = obj.optDouble("minTarget", 1.50)
            val maxT = obj.optDouble("maxTarget", 3.00)
            val cashout = obj.optDouble("cashoutTarget", 1.80)
            val risk = obj.optString("riskLevel", "MODERATE")
            val conf = obj.optInt("confidenceScore", 82)
            val pattern = obj.optString("patternDetected", "AI Pattern Sequence")
            val reasoning = obj.optString("reasoning", "Analyzed historical multiplier distribution.")

            GeminiAnalysisResult(
                predictedMultiplier = predicted,
                minTarget = minT,
                maxTarget = maxT,
                cashoutTarget = cashout,
                riskLevel = risk,
                confidenceScore = conf,
                patternDetected = pattern,
                reasoning = reasoning
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini analysis JSON", e)
            null
        }
    }
}
