package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
import com.example.ui.theme.AviationPurpleBorder
import com.example.ui.theme.AviationPurpleContainer
import com.example.ui.theme.AviationPurplePrimary
import com.example.ui.theme.AviationSurface
import com.example.ui.theme.AviationSurfaceVariant
import com.example.ui.theme.AviationTextMuted
import com.example.ui.theme.AviationTextPrimary
import com.example.ui.theme.AviationTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CockpitDashboardScreen(
    signals: List<SignalItem>,
    recentRounds: List<HistoricalRound>,
    metrics: AccuracyMetrics,
    selectedTeamMember: String,
    isScanningAi: Boolean,
    onPredictSignal: (useGemini: Boolean) -> Unit,
    onAddRoundMultiplier: (Double) -> Unit,
    onOpenTeamSelector: () -> Unit,
    modifier: Modifier = Modifier,
    onRefreshPredictions: () -> Unit = { onPredictSignal(true) }
) {
    val latestSignal = signals.firstOrNull()
    var inputMultiplierText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    PullToRefreshBox(
        isRefreshing = isScanningAi,
        onRefresh = onRefreshPredictions,
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_pull_to_refresh")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AviationBackground)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Immersive Top Flight Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$selectedTeamMember Squad",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationCyan,
                    letterSpacing = 1.5.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = "AeroPredict Logo",
                        tint = AviationTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AeroPredict AI",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextPrimary
                    )
                }
            }

            // Profile Settings Access Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AviationSurfaceVariant)
                    .border(1.dp, AviationPurpleBorder, RoundedCornerShape(12.dp))
                    .clickable { onOpenTeamSelector() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("btn_dashboard_profile_settings")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AviationPurpleContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile Settings",
                            tint = AviationCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "PROFILE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationCyan
                        )
                        Text(
                            text = "Settings",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AviationTextPrimary
                        )
                    }
                }
            }
        }

        // Quick Accuracy Telemetry Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryTile(
                modifier = Modifier.weight(1f),
                title = "WIN RATE",
                value = "${String.format("%.1f", metrics.winRatePercentage)}%",
                accentColor = AviationEmerald
            )
            TelemetryTile(
                modifier = Modifier.weight(1f),
                title = "WIN STREAK",
                value = "${metrics.currentWinStreak} Hits",
                accentColor = AviationGold
            )
            TelemetryTile(
                modifier = Modifier.weight(1f),
                title = "AVG MULT",
                value = "${String.format("%.2f", metrics.averageMultiplier)}x",
                accentColor = AviationCyan
            )
        }

        // Real-Time Multiplier Prediction & Live AI Dashboard Component
        MainPredictionCard(
            latestSignal = latestSignal,
            isScanningAi = isScanningAi,
            onPredictSignal = onPredictSignal
        )

        // Team Accuracy Dashboard Card (Displays Win Rate % & Total Signals Processed)
        TeamAccuracyCard(
            metrics = metrics,
            selectedTeamMember = selectedTeamMember
        )

        // Recent Multiplier Sequence Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AviationBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Sequence",
                            tint = AviationCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SIGNAL HISTORY SEQUENCE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationTextPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "${recentRounds.size} Rounds",
                        fontSize = 11.sp,
                        color = AviationTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(recentRounds) { round ->
                        RoundMultiplierPill(multiplier = round.multiplier)
                    }
                }
            }
        }

        // Quick Input Keypad for Entering Round Results
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AviationBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LOG ROUND RESULT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Enter actual Aviator crash multiplier to evaluate active signals",
                    fontSize = 11.sp,
                    color = AviationTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputMultiplierText,
                        onValueChange = { inputMultiplierText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_round_multiplier"),
                        placeholder = { Text("e.g. 2.84", color = AviationTextMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val value = inputMultiplierText.toDoubleOrNull()
                                if (value != null && value >= 1.0) {
                                    onAddRoundMultiplier(value)
                                    inputMultiplierText = ""
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AviationSurfaceVariant,
                            unfocusedContainerColor = AviationSurfaceVariant,
                            focusedBorderColor = AviationCyan,
                            unfocusedBorderColor = AviationBorder,
                            focusedTextColor = AviationTextPrimary,
                            unfocusedTextColor = AviationTextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            val value = inputMultiplierText.toDoubleOrNull()
                            if (value != null && value >= 1.0) {
                                onAddRoundMultiplier(value)
                                inputMultiplierText = ""
                            }
                        },
                        modifier = Modifier.testTag("btn_log_multiplier"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AviationEmerald)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LOG", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Preset Multiplier Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1.20, 1.50, 2.00, 3.50, 5.00, 10.00).forEach { preset ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AviationSurfaceVariant)
                                .border(1.dp, AviationBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    onAddRoundMultiplier(preset)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${preset}x",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationCyan
                            )
                        }
                    }
                }
            }
        }
    }
}
}

/**
 * Main Dashboard UI Component displaying real-time multiplier prediction,
 * circular progress/radar indicator for next signal countdown/scan, and state-change animations.
 */
@Composable
fun MainPredictionCard(
    latestSignal: SignalItem?,
    isScanningAi: Boolean,
    onPredictSignal: (useGemini: Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("main_prediction_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AviationSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(AviationPurpleBorder, AviationBorder)
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Live AI Status Indicator Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "REAL-TIME PREDICTION ENGINE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Pull down dashboard to force update AI predictions",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = AviationCyan.copy(alpha = 0.85f)
                    )
                }

                PulsingLiveAiBadge(isScanning = isScanningAi)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prediction Canvas & Circular Progress Indicator Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Soft Aura Glow
                val purplePrimaryColor = AviationPurplePrimary
                val purpleContainerColor = AviationPurpleContainer
                Canvas(modifier = Modifier.size(190.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                purplePrimaryColor.copy(alpha = 0.25f),
                                purpleContainerColor.copy(alpha = 0.10f),
                                Color.Transparent
                            )
                        ),
                        radius = size.width / 2
                    )
                }

                // Animated Circular Progress & Radar Ring
                AnimatedRadarCanvas(isScanning = isScanningAi)

                // Animated Content State Transition for Real-time Prediction Display
                AnimatedContent(
                    targetState = Pair(isScanningAi, latestSignal?.recommendedCashout),
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.85f, animationSpec = spring()))
                            .togetherWith(fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 1.1f))
                    },
                    label = "PredictionAnimation"
                ) { (scanning, cashout) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (scanning) {
                            CircularProgressIndicator(
                                color = AviationCyan,
                                modifier = Modifier.size(44.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "ANALYZING SIGNAL...",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationCyan,
                                letterSpacing = 1.sp
                            )
                        } else if (latestSignal != null && cashout != null) {
                            Text(
                                text = "Expected Next Signal",
                                fontSize = 12.sp,
                                color = AviationTextSecondary
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format("%.2f", cashout),
                                    fontSize = 58.sp,
                                    fontWeight = FontWeight.Light,
                                    color = AviationCyan,
                                    letterSpacing = (-1).sp
                                )
                                Text(
                                    text = "x",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AviationCyan,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            Text(
                                text = "TARGET RANGE: ${latestSignal.minTargetMultiplier}x - ${latestSignal.maxTargetMultiplier}x",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationTextMuted
                            )
                        } else {
                            Text(
                                text = "READY FOR SIGNAL",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap Predict or Scan to generate AI target",
                                fontSize = 11.sp,
                                color = AviationTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AI Analysis Status Bar (Confidence, Volatility, Pattern)
            if (latestSignal != null && !isScanningAi) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AviationSurfaceVariant)
                        .border(1.dp, AviationBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "CONFIDENCE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationTextMuted
                            )
                            Text(
                                text = "${latestSignal.confidenceScore}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationTextPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(AviationBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "RISK / VOLATILITY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationTextMuted
                            )
                            Text(
                                text = latestSignal.riskLevel,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = getRiskColor(latestSignal.riskLevel)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(28.dp)
                                .background(AviationBorder)
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "PATTERN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationTextMuted
                            )
                            Text(
                                text = latestSignal.patternDetected.take(16),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationCyan
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = latestSignal.notes,
                    fontSize = 11.sp,
                    color = AviationTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Signal Generation Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onPredictSignal(false) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_quick_predict"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AviationSurfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Quick Predict",
                        tint = AviationCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "QUICK SIGNAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextPrimary
                    )
                }

                Button(
                    onClick = { onPredictSignal(true) },
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("btn_gemini_predict"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AviationPurpleContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini AI",
                        tint = AviationCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "GEMINI AI SCAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationCyan
                    )
                }
            }
        }
    }
}

/**
 * Pulsing Live AI Badge with entrance & state animations.
 */
@Composable
fun PulsingLiveAiBadge(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "LivePulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isScanning) 400 else 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )

    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isScanning) 400 else 900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AviationSurfaceVariant)
            .border(1.dp, AviationBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
                    .clip(CircleShape)
                    .background(if (isScanning) AviationCyan else AviationEmerald)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isScanning) "SCANNING" else "LIVE AI",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isScanning) AviationCyan else AviationEmerald
            )
        }
    }
}

@Composable
fun TelemetryTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AviationSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AviationBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AviationTextMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }
    }
}

@Composable
fun AnimatedRadarCanvas(isScanning: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isScanning) 1200 else 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Sweep"
    )

    val border = AviationBorder
    val purplePrimary = AviationPurplePrimary
    val cyan = AviationCyan

    Canvas(modifier = Modifier.size(180.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2

        // Radar grid circles
        drawCircle(
            color = border.copy(alpha = 0.6f),
            radius = radius * 0.95f,
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = border.copy(alpha = 0.4f),
            radius = radius * 0.65f,
            style = Stroke(width = 1.dp.toPx())
        )
        drawCircle(
            color = border.copy(alpha = 0.2f),
            radius = radius * 0.35f,
            style = Stroke(width = 1.dp.toPx())
        )

        // Crosshairs
        drawLine(
            color = border.copy(alpha = 0.3f),
            start = Offset(0f, center.y),
            end = Offset(size.width, center.y),
            strokeWidth = 1.dp.toPx()
        )
        drawLine(
            color = border.copy(alpha = 0.3f),
            start = Offset(center.x, 0f),
            end = Offset(center.x, size.height),
            strokeWidth = 1.dp.toPx()
        )

        // Sweeping beam arc
        val beamBrush = Brush.sweepGradient(
            colors = listOf(
                purplePrimary.copy(alpha = 0.0f),
                purplePrimary.copy(alpha = 0.05f),
                purplePrimary.copy(alpha = 0.35f),
                cyan
            )
        )

        drawArc(
            brush = beamBrush,
            startAngle = sweepAngle - 90f,
            sweepAngle = 90f,
            useCenter = true,
            size = size
        )
    }
}

@Composable
fun DetailBadge(
    label: String,
    value: String,
    valueColor: Color = AviationTextPrimary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = AviationTextMuted
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
fun RoundMultiplierPill(multiplier: Double) {
    val (bgColor, textColor) = when {
        multiplier < 2.0 -> Pair(AviationSurfaceVariant, AviationTextSecondary)
        multiplier < 10.0 -> Pair(AviationEmerald.copy(alpha = 0.15f), AviationEmerald)
        else -> Pair(AviationGold.copy(alpha = 0.2f), AviationGold)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = "${multiplier}x",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun getRiskColor(riskLevel: String): Color {
    return when (riskLevel.uppercase()) {
        "SAFE" -> AviationEmerald
        "MODERATE" -> AviationAmber
        "MOONSHOT" -> AviationGold
        else -> AviationCyan
    }
}

/**
 * Prominent 'Team Accuracy' Dashboard Card displaying win rate percentage, total signals processed,
 * hit/miss/pending breakdown, and accuracy progress bar fetched from Room DB / State.
 */
@Composable
fun TeamAccuracyCard(
    metrics: AccuracyMetrics,
    selectedTeamMember: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("team_accuracy_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = AviationSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(AviationPurpleBorder, AviationBorder)
            )
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AviationPurpleContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Team Accuracy",
                            tint = AviationCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "TEAM ACCURACY DASHBOARD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationTextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "$selectedTeamMember Squad Analytics",
                            fontSize = 10.sp,
                            color = AviationCyan
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AviationEmerald.copy(alpha = 0.15f))
                        .border(1.dp, AviationEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ROOM DB ACTIVE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationEmerald,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Stat Row: Win Rate % + Total Signals Processed
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AviationSurfaceVariant)
                    .border(1.dp, AviationBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Win Rate Percentage Display
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "CURRENT WIN RATE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextMuted,
                        letterSpacing = 1.sp
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.1f", metrics.winRatePercentage),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AviationEmerald
                        )
                        Text(
                            text = "%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationEmerald,
                            modifier = Modifier.padding(bottom = 3.dp, start = 2.dp)
                        )
                    }
                }

                // Vertical Separator
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .width(1.dp)
                        .background(AviationBorder)
                )

                // Total Signals Processed Display
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "SIGNALS PROCESSED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${metrics.totalSignals}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AviationTextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Detailed Breakdown Grid: Hits, Misses, Pending
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AccuracyMiniTile(
                    label = "HITS",
                    value = "${metrics.hitCount}",
                    color = AviationEmerald,
                    modifier = Modifier.weight(1f)
                )
                AccuracyMiniTile(
                    label = "MISSES",
                    value = "${metrics.missCount}",
                    color = AviationCrimson,
                    modifier = Modifier.weight(1f)
                )
                AccuracyMiniTile(
                    label = "PENDING",
                    value = "${metrics.pendingCount}",
                    color = AviationAmber,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Accuracy Ratio Visual Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Evaluated Signal Accuracy",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AviationTextMuted
                    )
                    val evaluated = metrics.hitCount + metrics.missCount
                    Text(
                        text = "$evaluated Signals Evaluated",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                val evaluated = metrics.hitCount + metrics.missCount
                val progressRatio = if (evaluated > 0) metrics.hitCount.toFloat() / evaluated else 0f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AviationSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressRatio.coerceIn(0f, 1f))
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(AviationCyan, AviationEmerald)
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun AccuracyMiniTile(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AviationSurfaceVariant)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = AviationTextMuted,
                letterSpacing = 0.5.sp
            )
        }
    }
}
