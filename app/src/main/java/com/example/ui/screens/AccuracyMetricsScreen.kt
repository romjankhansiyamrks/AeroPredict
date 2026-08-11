package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.HistoricalRound
import com.example.data.db.SignalItem
import com.example.ui.AccuracyMetrics
import com.example.ui.theme.AviationAmber
import com.example.ui.theme.AviationBackground
import com.example.ui.theme.AviationBorder
import com.example.ui.theme.AviationCrimson
import com.example.ui.theme.AviationCyan
import com.example.ui.theme.AviationEmerald
import com.example.ui.theme.AviationGold
import com.example.ui.theme.AviationSurface
import com.example.ui.theme.AviationSurfaceVariant
import com.example.ui.theme.AviationTextMuted
import com.example.ui.theme.AviationTextPrimary
import com.example.ui.theme.AviationTextSecondary

@Composable
fun AccuracyMetricsScreen(
    metrics: AccuracyMetrics,
    recentRounds: List<HistoricalRound>,
    signals: List<SignalItem>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AviationBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = "Accuracy Telemetry",
                tint = AviationCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ACCURACY & TELEMETRY",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AviationTextPrimary,
                letterSpacing = 1.sp
            )
        }

        // Win Rate Ring Gauge Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(AviationCyan, AviationBorder)))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "OVERALL SIGNAL ACCURACY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularGauge(percentage = metrics.winRatePercentage)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${String.format("%.1f", metrics.winRatePercentage)}%",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AviationEmerald
                        )
                        Text(
                            text = "WIN RATE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricCounter(label = "TOTAL EVALS", value = "${metrics.totalSignals}")
                    MetricCounter(label = "HITS", value = "${metrics.hitCount}", valueColor = AviationEmerald)
                    MetricCounter(label = "MISSES", value = "${metrics.missCount}", valueColor = AviationCrimson)
                    MetricCounter(label = "PENDING", value = "${metrics.pendingCount}", valueColor = AviationAmber)
                }
            }
        }

        // Performance by Risk Tier Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Risk Tiers",
                        tint = AviationCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "WIN RATE BY RISK TIER",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                RiskProgressRow(
                    label = "SAFE TIER (Low Volatility)",
                    winRate = metrics.safeWinRate,
                    barColor = AviationEmerald
                )

                Spacer(modifier = Modifier.height(12.dp))

                RiskProgressRow(
                    label = "MODERATE TIER (Standard Wave)",
                    winRate = metrics.moderateWinRate,
                    barColor = AviationAmber
                )

                Spacer(modifier = Modifier.height(12.dp))

                RiskProgressRow(
                    label = "MOONSHOT TIER (High Yield)",
                    winRate = metrics.moonshotWinRate,
                    barColor = AviationGold
                )
            }
        }

        // Multiplier Distribution Canvas Bar Chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = "Distribution",
                        tint = AviationCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HISTORICAL MULTIPLIER DISTRIBUTION",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                MultiplierDistributionChart(rounds = recentRounds)
            }
        }

        // Team Trophy & Record Highlights
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Best Hit",
                        tint = AviationGold,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "BEST HIT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AviationTextMuted)
                        Text(
                            text = "${String.format("%.2f", metrics.bestHitMultiplier)}x",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AviationGold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(AviationBorder)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Streak",
                        tint = AviationEmerald,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "ACTIVE STREAK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AviationTextMuted)
                        Text(
                            text = "${metrics.currentWinStreak} Hits",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AviationEmerald
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircularGauge(percentage: Float) {
    val surfaceVariantColor = AviationSurfaceVariant
    val cyanColor = AviationCyan
    val emeraldColor = AviationEmerald

    Canvas(modifier = Modifier.size(160.dp)) {
        val sweepAngle = (percentage / 100f) * 260f
        val startAngle = 140f
        val strokeWidth = 14.dp.toPx()

        // Background Arc
        drawArc(
            color = surfaceVariantColor,
            startAngle = startAngle,
            sweepAngle = 260f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Progress Arc
        drawArc(
            brush = Brush.sweepGradient(listOf(cyanColor, emeraldColor)),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun MetricCounter(label: String, value: String, valueColor: Color = AviationTextPrimary) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AviationTextMuted)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
    }
}

@Composable
fun RiskProgressRow(label: String, winRate: Float, barColor: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = AviationTextSecondary)
            Text(
                text = "${String.format("%.1f", winRate)}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = barColor
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { (winRate / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = barColor,
            trackColor = AviationSurfaceVariant
        )
    }
}

@Composable
fun MultiplierDistributionChart(rounds: List<HistoricalRound>) {
    val total = maxOf(rounds.size, 1)

    val bin1 = rounds.count { it.multiplier >= 1.0 && it.multiplier < 1.5 }
    val bin2 = rounds.count { it.multiplier >= 1.5 && it.multiplier < 2.0 }
    val bin3 = rounds.count { it.multiplier >= 2.0 && it.multiplier < 5.0 }
    val bin4 = rounds.count { it.multiplier >= 5.0 && it.multiplier < 10.0 }
    val bin5 = rounds.count { it.multiplier >= 10.0 }

    val bins = listOf(
        Pair("1.0-1.5x", bin1),
        Pair("1.5-2.0x", bin2),
        Pair("2.0-5.0x", bin3),
        Pair("5.0-10x", bin4),
        Pair("10x+", bin5)
    )

    val maxCount = maxOf(bins.maxOf { it.second }, 1)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        bins.forEach { (range, count) ->
            val ratio = count.toFloat() / maxCount.toFloat()
            val percent = (count.toFloat() / total.toFloat()) * 100f

            val barColor = when {
                range.contains("1.0") -> AviationSurfaceVariant
                range.contains("1.5") -> AviationCyan
                range.contains("2.0") -> AviationEmerald
                range.contains("5.0") -> AviationAmber
                else -> AviationGold
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = range,
                    fontSize = 11.sp,
                    color = AviationTextSecondary,
                    modifier = Modifier.width(64.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AviationSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(fraction = maxOf(ratio, 0.03f))
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$count (${String.format("%.0f", percent)}%)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextPrimary,
                    modifier = Modifier.width(60.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
