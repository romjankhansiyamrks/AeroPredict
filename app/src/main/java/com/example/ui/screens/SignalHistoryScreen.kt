package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SignalItem
import com.example.ui.SignalFilter
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SignalHistoryScreen(
    signals: List<SignalItem>,
    selectedFilter: SignalFilter,
    onFilterSelected: (SignalFilter) -> Unit,
    onRecordOutcome: (signalId: Int, actualMultiplier: Double) -> Unit,
    onDeleteSignal: (Int) -> Unit,
    onClearAllHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editingSignal by remember { mutableStateOf<SignalItem?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AviationBackground)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Signal History",
                    tint = AviationCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SIGNAL HISTORY & LOGS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextPrimary,
                    letterSpacing = 1.sp
                )
            }

            if (signals.isNotEmpty()) {
                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.testTag("btn_clear_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear History",
                        tint = AviationCrimson
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Tabs Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SignalFilter.entries.forEach { filter ->
                val isSelected = filter == selectedFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterSelected(filter) },
                    label = {
                        Text(
                            text = when (filter) {
                                SignalFilter.ALL -> "ALL (${signals.size})"
                                SignalFilter.HITS -> "HITS"
                                SignalFilter.MISSES -> "MISSES"
                                SignalFilter.PENDING -> "PENDING"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AviationCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = AviationSurfaceVariant,
                        labelColor = AviationTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = AviationBorder,
                        selectedBorderColor = AviationCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (signals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "No Signals",
                        tint = AviationTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Signals Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextSecondary
                    )
                    Text(
                        text = "Generate a signal from the Cockpit dashboard to start tracking accuracy telemetry.",
                        fontSize = 12.sp,
                        color = AviationTextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(signals, key = { it.id }) { signal ->
                    SignalCardItem(
                        signal = signal,
                        onEvaluateClick = { editingSignal = signal },
                        onDeleteClick = { onDeleteSignal(signal.id) }
                    )
                }
            }
        }
    }

    // Modal Dialog to record actual multiplier for pending or updating signals
    editingSignal?.let { signal ->
        RecordOutcomeDialog(
            signal = signal,
            onDismiss = { editingSignal = null },
            onConfirm = { actual ->
                onRecordOutcome(signal.id, actual)
                editingSignal = null
            }
        )
    }

    // Confirm Clear Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Signal History?", color = AviationTextPrimary) },
            text = { Text("This will permanently delete all historical signals and accuracy metrics.", color = AviationTextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAllHistory()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear All", color = AviationCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = AviationTextSecondary)
                }
            },
            containerColor = AviationSurface
        )
    }
}

@Composable
fun SignalCardItem(
    signal: SignalItem,
    onEvaluateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss · MMM dd", Locale.getDefault()) }
    val formattedTime = remember(signal.timestamp) { dateFormat.format(Date(signal.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("signal_card_${signal.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AviationSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AviationBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = signal.status)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Target: ${signal.recommendedCashout}x",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AviationTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RiskChip(riskLevel = signal.riskLevel)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete",
                            tint = AviationTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Range: ${signal.minTargetMultiplier}x - ${signal.maxTargetMultiplier}x",
                        fontSize = 12.sp,
                        color = AviationTextSecondary
                    )
                    Text(
                        text = "Pattern: ${signal.patternDetected}",
                        fontSize = 11.sp,
                        color = AviationTextMuted
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Actual: ${signal.actualMultiplier?.let { "${it}x" } ?: "Pending"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (signal.status) {
                            "HIT" -> AviationEmerald
                            "MISS" -> AviationCrimson
                            else -> AviationAmber
                        }
                    )
                    Text(
                        text = "Confidence: ${signal.confidenceScore}%",
                        fontSize = 11.sp,
                        color = AviationEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Tag & Evaluate Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$formattedTime • ${signal.teamMember}",
                    fontSize = 10.sp,
                    color = AviationTextMuted
                )

                Button(
                    onClick = onEvaluateClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AviationSurfaceVariant),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Log Result",
                        tint = AviationCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (signal.status == "PENDING") "RECORD RESULT" else "UPDATE RESULT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationCyan
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor, icon) = when (status) {
        "HIT" -> Triple(AviationEmerald.copy(alpha = 0.2f), AviationEmerald, Icons.Default.CheckCircle)
        "MISS" -> Triple(AviationCrimson.copy(alpha = 0.2f), AviationCrimson, Icons.Default.Close)
        else -> Triple(AviationAmber.copy(alpha = 0.2f), AviationAmber, Icons.Default.HourglassTop)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = status,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}

@Composable
fun RiskChip(riskLevel: String) {
    val color = getRiskColor(riskLevel)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = riskLevel,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun RecordOutcomeDialog(
    signal: SignalItem,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var textValue by remember { mutableStateOf(signal.actualMultiplier?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Round Outcome", color = AviationTextPrimary) },
        text = {
            Column {
                Text(
                    text = "Target Cashout: ${signal.recommendedCashout}x",
                    fontSize = 12.sp,
                    color = AviationCyan
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    label = { Text("Actual Crash Multiplier (e.g. 2.15)", color = AviationTextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val v = textValue.toDoubleOrNull()
                            if (v != null && v >= 1.0) onConfirm(v)
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AviationSurfaceVariant,
                        unfocusedContainerColor = AviationSurfaceVariant,
                        focusedBorderColor = AviationCyan,
                        unfocusedBorderColor = AviationBorder,
                        focusedTextColor = AviationTextPrimary,
                        unfocusedTextColor = AviationTextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = textValue.toDoubleOrNull()
                    if (v != null && v >= 1.0) onConfirm(v)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AviationEmerald)
            ) {
                Text("Save Result", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AviationTextSecondary)
            }
        },
        containerColor = AviationSurface
    )
}
