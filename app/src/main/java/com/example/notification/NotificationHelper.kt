package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.db.SignalItem

object NotificationHelper {

    const val CHANNEL_ID = "high_confidence_signals"
    private const val CHANNEL_NAME = "High-Confidence AI Signals"
    private const val CHANNEL_DESC = "Real-time push alerts when the AI analysis engine detects high-probability flight multiplier signals."
    private const val NOTIFICATION_ID_BASE = 1000

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
                lightColor = android.graphics.Color.CYAN
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showHighConfidenceSignalNotification(context: Context, signal: SignalItem) {
        createNotificationChannel(context)

        if (!hasNotificationPermission(context)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("SIGNAL_ID", signal.id)
            putExtra("SIGNAL_CONFIDENCE", signal.confidenceScore)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            if (signal.id != 0) signal.id else System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isHighMultiplier = signal.predictedMultiplier >= 5.0 || signal.recommendedCashout >= 5.0
        val title = if (isHighMultiplier) {
            "🔥 HIGH-PROBABILITY SIGNAL: ${signal.predictedMultiplier}x (${signal.confidenceScore}% Confidence)"
        } else {
            "🚨 High-Confidence Signal: ${signal.predictedMultiplier}x (${signal.confidenceScore}% Confidence)"
        }
        val shortText = "Target: ${signal.recommendedCashout}x • Risk: ${signal.riskLevel} • ${signal.patternDetected}"

        val bigText = """
            🎯 Recommended Cashout: ${signal.recommendedCashout}x
            📊 Confidence Score: ${signal.confidenceScore}%
            ⚡ Risk Tier: ${signal.riskLevel}
            🔍 Pattern Detected: ${signal.patternDetected}
            👤 Officer Squad: ${signal.teamMember}
            
            Stay informed even when in background! Tap to enter Radar Cockpit.
        """.trimIndent()

        val notificationId = if (signal.id != 0) NOTIFICATION_ID_BASE + signal.id else NOTIFICATION_ID_BASE + (1..999).random()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(shortText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(0xFF00E5FF.toInt())
            .setVibrate(longArrayOf(0, 250, 100, 250))
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Runtime permission not granted
        }
    }

    fun showTestNotification(context: Context) {
        val testSignal = SignalItem(
            id = (900..999).random(),
            predictedMultiplier = 3.85,
            minTargetMultiplier = 3.00,
            maxTargetMultiplier = 5.20,
            recommendedCashout = 3.40,
            riskLevel = "SAFE",
            confidenceScore = 94,
            patternDetected = "Tactical Quantum Delta Rebound",
            teamMember = "Alpha Flight Command",
            notes = "Test push alert initiated by flight strategist."
        )
        showHighConfidenceSignalNotification(context, testSignal)
    }
}
