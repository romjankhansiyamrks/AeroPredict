package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.HistoricalRound
import com.example.data.db.SignalItem
import com.example.data.repository.AviatorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AccuracyMetrics(
    val totalSignals: Int = 0,
    val hitCount: Int = 0,
    val missCount: Int = 0,
    val pendingCount: Int = 0,
    val winRatePercentage: Float = 0f,
    val safeWinRate: Float = 0f,
    val moderateWinRate: Float = 0f,
    val moonshotWinRate: Float = 0f,
    val bestHitMultiplier: Double = 0.0,
    val currentWinStreak: Int = 0,
    val averageMultiplier: Double = 0.0
)

enum class SignalFilter {
    ALL, HITS, MISSES, PENDING
}

class AviatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AviatorRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AviatorRepository(database.signalDao())
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    val allSignals: StateFlow<List<SignalItem>> = repository.allSignals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentRounds: StateFlow<List<HistoricalRound>> = repository.recentRounds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedFilter = MutableStateFlow(SignalFilter.ALL)
    val selectedTeamMember = MutableStateFlow("Alpha Flight")
    val isScanningAi = MutableStateFlow(false)
    val quickInputText = MutableStateFlow("")
    val toastMessage = MutableStateFlow<String?>(null)

    // Push Notification & Background Radar Alert State
    val isDarkTheme = MutableStateFlow(true)
    val pushNotificationsEnabled = MutableStateFlow(true)
    val confidenceThreshold = MutableStateFlow(80) // Alert threshold percentage (e.g. 80%)
    val backgroundScanEnabled = MutableStateFlow(true)
    val lastAlertSignal = MutableStateFlow<SignalItem?>(null)

    // Security & Profile System State
    val isLoggedIn = MutableStateFlow(false)
    val userEmail = MutableStateFlow("romjankhansiyamrksl@gmail.com")
    val accountCreationDate = MutableStateFlow("August 8, 2026")
    val loginProvider = MutableStateFlow("Google Account")
    val userPin = MutableStateFlow("7777")
    val officerName = MutableStateFlow("Romjan Khan Siyam")
    val officerCallsign = MutableStateFlow("Vortex-1")
    val squadRole = MutableStateFlow("Chief Flight Strategist")
    val clearanceLevel = MutableStateFlow("Level 5 Command")
    val biometricsEnabled = MutableStateFlow(true)
    val autoLockOnIdle = MutableStateFlow(true)
    val securityLogs = MutableStateFlow(
        listOf(
            "System Boot: Tactical Security Active",
            "Initial Clearance Level 5 Verified",
            "Encrypted Signal Channel Ready"
        )
    )

    // Account Store for Demo Authentication
    private val registeredUsers = mutableMapOf(
        "romjankhansiyamrksl@gmail.com" to "123456",
        "romjamrash@gmail.com" to "123456",
        "demo@aistudio.com" to "password123"
    )

    fun loginWithGoogle(customEmail: String? = null) {
        val emailToUse = customEmail ?: "romjankhansiyamrksl@gmail.com"
        userEmail.value = emailToUse
        loginProvider.value = "Google OAuth2"
        officerName.value = emailToUse.substringBefore("@").replace(".", " ").capitalize()
        officerCallsign.value = "Commander " + emailToUse.substringBefore("@").take(6).uppercase()
        isLoggedIn.value = true
        addSecurityLog("Google Sign In Verified: $emailToUse")
        toastMessage.value = "Signed in successfully via Google Account ($emailToUse)"
    }

    fun loginWithEmail(email: String, pass: String): Boolean {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || pass.isBlank()) {
            toastMessage.value = "Please enter both Email and Password"
            return false
        }

        // Demo check: If user exists or if it's a valid email format, allow login
        val expectedPass = registeredUsers[trimmedEmail]
        if (expectedPass != null && expectedPass != pass) {
            addSecurityLog("Failed Email Login Attempt for $trimmedEmail")
            toastMessage.value = "Incorrect Password for $trimmedEmail"
            return false
        }

        // Register on the fly if new email
        registeredUsers[trimmedEmail] = pass
        userEmail.value = trimmedEmail
        loginProvider.value = "Email & Password"
        officerName.value = trimmedEmail.substringBefore("@").replace(".", " ").capitalize()
        officerCallsign.value = "Officer " + trimmedEmail.substringBefore("@").take(6).uppercase()
        isLoggedIn.value = true
        addSecurityLog("Email Authentication Granted: $trimmedEmail")
        toastMessage.value = "Welcome back, $trimmedEmail!"
        return true
    }

    fun createAccount(fullName: String, email: String, pass: String): Boolean {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || pass.length < 4) {
            toastMessage.value = "Invalid input. Password must be at least 4 characters."
            return false
        }

        registeredUsers[trimmedEmail] = pass
        userEmail.value = trimmedEmail
        loginProvider.value = "Email & Password"
        officerName.value = fullName.ifBlank { trimmedEmail.substringBefore("@") }
        officerCallsign.value = "Officer " + trimmedEmail.substringBefore("@").take(6).uppercase()
        isLoggedIn.value = true
        addSecurityLog("New Account Registered: $trimmedEmail")
        toastMessage.value = "Account created successfully for $trimmedEmail!"
        return true
    }

    fun sendPasswordReset(email: String): Boolean {
        val trimmedEmail = email.trim()
        if (trimmedEmail.contains("@")) {
            addSecurityLog("Password Reset Link sent to $trimmedEmail")
            toastMessage.value = "Password reset instructions sent to $trimmedEmail"
            return true
        } else {
            toastMessage.value = "Please enter a valid Gmail / Email address"
            return false
        }
    }

    fun verifyPin(inputPin: String): Boolean {
        if (inputPin == userPin.value) {
            isLoggedIn.value = true
            addSecurityLog("PIN Authentication Granted [Callsign: ${officerCallsign.value}]")
            toastMessage.value = "Security Clearance Granted. Welcome ${officerCallsign.value}!"
            return true
        } else {
            addSecurityLog("FAILED Authentication Attempt with PIN '$inputPin'")
            toastMessage.value = "Access Denied: Invalid Security PIN"
            return false
        }
    }

    fun loginWithBiometrics() {
        if (biometricsEnabled.value) {
            isLoggedIn.value = true
            addSecurityLog("Biometric Scan Verified [Callsign: ${officerCallsign.value}]")
            toastMessage.value = "Biometric Match Confirmed. Access Granted!"
        } else {
            toastMessage.value = "Biometric Login Disabled in Security Settings"
        }
    }

    fun logout() {
        isLoggedIn.value = false
        addSecurityLog("Officer Logged Out - System Locked")
        toastMessage.value = "System Locked. Security Clearance Terminated."
    }

    fun updateOfficerProfile(newName: String, newCallsign: String, newRole: String) {
        officerName.value = newName.ifBlank { officerName.value }
        officerCallsign.value = newCallsign.ifBlank { officerCallsign.value }
        squadRole.value = newRole.ifBlank { squadRole.value }
        addSecurityLog("Profile Updated: Callsign set to ${officerCallsign.value}")
        toastMessage.value = "Officer Profile Updated Successfully"
    }

    fun updatePin(oldPin: String, newPin: String): Boolean {
        if (oldPin == userPin.value && newPin.length >= 4) {
            userPin.value = newPin
            addSecurityLog("Security Key / PIN Changed Successfully")
            toastMessage.value = "Security PIN Updated Successfully"
            return true
        } else {
            toastMessage.value = "PIN Update Failed: Invalid Current PIN or PIN under 4 digits"
            return false
        }
    }

    fun toggleBiometrics(enabled: Boolean) {
        biometricsEnabled.value = enabled
        addSecurityLog("Biometric Sensor state set to: ${if (enabled) "ENABLED" else "DISABLED"}")
    }

    fun toggleAutoLock(enabled: Boolean) {
        autoLockOnIdle.value = enabled
        addSecurityLog("Auto-Lock state set to: ${if (enabled) "ENABLED" else "DISABLED"}")
    }

    private fun addSecurityLog(entry: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        val newLog = "[$timestamp] $entry"
        securityLogs.value = (listOf(newLog) + securityLogs.value).take(15)
    }

    val filteredSignals: StateFlow<List<SignalItem>> = combine(allSignals, selectedFilter) { signals, filter ->
        when (filter) {
            SignalFilter.ALL -> signals
            SignalFilter.HITS -> signals.filter { it.status == "HIT" }
            SignalFilter.MISSES -> signals.filter { it.status == "MISS" }
            SignalFilter.PENDING -> signals.filter { it.status == "PENDING" }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val accuracyMetrics: StateFlow<AccuracyMetrics> = allSignals.combine(recentRounds) { signals, rounds ->
        calculateMetrics(signals, rounds)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AccuracyMetrics())

    fun generateSignal(useGeminiAi: Boolean = true) {
        viewModelScope.launch {
            isScanningAi.value = true
            try {
                val newSignal = repository.generateNewSignal(selectedTeamMember.value, useGeminiAi)
                toastMessage.value = "New ${newSignal.riskLevel} signal generated! Target ${newSignal.recommendedCashout}x"

                // Check high-confidence or high-probability (>=5.0x) alert conditions
                val isHighProbability = newSignal.predictedMultiplier >= 5.0 || newSignal.recommendedCashout >= 5.0 || newSignal.confidenceScore >= confidenceThreshold.value
                if (pushNotificationsEnabled.value && isHighProbability) {
                    lastAlertSignal.value = newSignal
                    com.example.notification.NotificationHelper.showHighConfidenceSignalNotification(
                        getApplication(),
                        newSignal
                    )
                    addSecurityLog("High-Probability Alert Triggered (${newSignal.predictedMultiplier}x / ${newSignal.confidenceScore}% confidence)")
                }
            } catch (e: Exception) {
                toastMessage.value = "Signal generation error: ${e.message}"
            } finally {
                isScanningAi.value = false
            }
        }
    }

    fun toggleDarkTheme(enabled: Boolean) {
        isDarkTheme.value = enabled
        addSecurityLog("UI Theme Switched to ${if (enabled) "Dark Mode" else "Light Mode"}")
        toastMessage.value = "Theme updated: ${if (enabled) "Dark" else "Light"} Mode"
    }

    fun togglePushNotifications(enabled: Boolean) {
        pushNotificationsEnabled.value = enabled
        addSecurityLog("High-Confidence Push Notifications set to: ${if (enabled) "ENABLED" else "DISABLED"}")
        toastMessage.value = if (enabled) "Push Alerts Activated" else "Push Alerts Deactivated"
    }

    fun setConfidenceThreshold(threshold: Int) {
        confidenceThreshold.value = threshold
        addSecurityLog("Push Alert Confidence Threshold updated to $threshold%")
        toastMessage.value = "Alert Threshold set to $threshold% Confidence"
    }

    fun toggleBackgroundScan(enabled: Boolean) {
        backgroundScanEnabled.value = enabled
        addSecurityLog("Background AI Radar Scanning set to: ${if (enabled) "ACTIVE" else "PAUSED"}")
    }

    fun triggerTestHighConfidenceNotification() {
        com.example.notification.NotificationHelper.showTestNotification(getApplication())
        addSecurityLog("Manual Test Push Notification Dispatched")
        toastMessage.value = "Dispatched High-Confidence Test Push Alert!"
    }

    fun addRoundMultiplier(multiplier: Double) {
        viewModelScope.launch {
            repository.addHistoricalRound(multiplier, selectedTeamMember.value)
            toastMessage.value = "Logged round multiplier: ${multiplier}x"
        }
    }

    fun recordSignalOutcome(signalId: Int, actualMultiplier: Double) {
        viewModelScope.launch {
            repository.updateSignalOutcome(signalId, actualMultiplier)
            toastMessage.value = "Recorded outcome: ${actualMultiplier}x"
        }
    }

    fun deleteSignal(id: Int) {
        viewModelScope.launch {
            repository.deleteSignal(id)
        }
    }

    fun deleteRound(id: Int) {
        viewModelScope.launch {
            repository.deleteRound(id)
        }
    }

    fun clearToast() {
        toastMessage.value = null
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllData()
            toastMessage.value = "Cleared all history & telemetry"
        }
    }

    private fun calculateMetrics(signals: List<SignalItem>, rounds: List<HistoricalRound>): AccuracyMetrics {
        val completed = signals.filter { it.status == "HIT" || it.status == "MISS" }
        val hits = completed.count { it.status == "HIT" }
        val misses = completed.count { it.status == "MISS" }
        val pending = signals.count { it.status == "PENDING" }
        val total = completed.size

        val winRate = if (total > 0) (hits.toFloat() / total) * 100f else 0f

        // Win rates by risk tier
        val safeSignals = completed.filter { it.riskLevel == "SAFE" }
        val safeWinRate = if (safeSignals.isNotEmpty()) (safeSignals.count { it.status == "HIT" }.toFloat() / safeSignals.size) * 100f else 0f

        val modSignals = completed.filter { it.riskLevel == "MODERATE" }
        val modWinRate = if (modSignals.isNotEmpty()) (modSignals.count { it.status == "HIT" }.toFloat() / modSignals.size) * 100f else 0f

        val moonSignals = completed.filter { it.riskLevel == "MOONSHOT" }
        val moonWinRate = if (moonSignals.isNotEmpty()) (moonSignals.count { it.status == "HIT" }.toFloat() / moonSignals.size) * 100f else 0f

        val bestHit = signals.mapNotNull { it.actualMultiplier }.maxOrNull() ?: 0.0

        // Calculate win streak
        var streak = 0
        for (signal in completed) {
            if (signal.status == "HIT") {
                streak++
            } else {
                break
            }
        }

        val avgMult = if (rounds.isNotEmpty()) rounds.map { it.multiplier }.average() else 0.0

        return AccuracyMetrics(
            totalSignals = signals.size,
            hitCount = hits,
            missCount = misses,
            pendingCount = pending,
            winRatePercentage = winRate,
            safeWinRate = safeWinRate,
            moderateWinRate = modWinRate,
            moonshotWinRate = moonWinRate,
            bestHitMultiplier = bestHit,
            currentWinStreak = streak,
            averageMultiplier = avgMult
        )
    }
}
