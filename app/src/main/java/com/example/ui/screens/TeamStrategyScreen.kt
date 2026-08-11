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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AviationAmber
import com.example.ui.theme.AviationBackground
import com.example.ui.theme.AviationBorder
import com.example.ui.theme.AviationCyan
import com.example.ui.theme.AviationEmerald
import com.example.ui.theme.AviationGold
import com.example.ui.theme.AviationSurface
import com.example.ui.theme.AviationSurfaceVariant
import com.example.ui.theme.AviationTextMuted
import com.example.ui.theme.AviationTextPrimary
import com.example.ui.theme.AviationTextSecondary

@Composable
fun TeamStrategyScreen(
    selectedTeamMember: String,
    onSelectTeamMember: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val teamSquads = listOf("Alpha Flight", "Beta Squadron", "VIP Flight Crew", "Tactical Ops")
    val squadMembers = listOf(
        Pair("Capt. Alex", "Flight Lead • 88% Win Rate"),
        Pair("Officer Sarah", "Tactical Analyst • 84% Win Rate"),
        Pair("Marcus V.", "Signal Specialist • 91% Win Rate"),
        Pair("Devon K.", "Data Telemetry • 82% Win Rate")
    )

    var targetCashoutThreshold by remember { mutableFloatStateOf(1.80f) }
    var riskTolerance by remember { mutableStateOf("BALANCED") }
    var teamNotes by remember { mutableStateOf("Focus on 1.80x-2.20x low volatility clusters during early session.") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AviationBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = "Team Hub",
                tint = AviationCyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "TEAM STRATEGY & SQUAD HUB",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AviationTextPrimary,
                letterSpacing = 1.sp
            )
        }

        // Active Team Selection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AviationBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SELECT ACTIVE SQUAD / TEAM",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    teamSquads.forEach { squad ->
                        val isSelected = squad == selectedTeamMember
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AviationSurfaceVariant else AviationSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AviationCyan else AviationBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectTeamMember(squad) }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = squad,
                                        tint = if (isSelected) AviationCyan else AviationTextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = squad,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) AviationCyan else AviationTextPrimary
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = AviationEmerald
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Team Playbook & Cashout Parameter Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Parameters",
                        tint = AviationCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TEAM STRATEGY PLAYBOOK",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Target Cashout Threshold Slider
                Text(
                    text = "TARGET CASHOUT THRESHOLD: ${String.format("%.2f", targetCashoutThreshold)}x",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextSecondary
                )
                Slider(
                    value = targetCashoutThreshold,
                    onValueChange = { targetCashoutThreshold = it },
                    valueRange = 1.20f..5.00f,
                    colors = SliderDefaults.colors(
                        thumbColor = AviationCyan,
                        activeTrackColor = AviationCyan,
                        inactiveTrackColor = AviationSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Risk Tolerance Selector
                Text(
                    text = "RISK TOLERANCE MODE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("CONSERVATIVE", "BALANCED", "AGGRESSIVE").forEach { mode ->
                        val isSelected = mode == riskTolerance
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AviationSurfaceVariant else AviationSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) AviationCyan else AviationBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { riskTolerance = mode }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) AviationCyan else AviationTextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Team Tactical Notes
                Text(
                    text = "TEAM TACTICAL NOTES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = teamNotes,
                    onValueChange = { teamNotes = it },
                    modifier = Modifier.fillMaxWidth(),
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
        }

        // Gemini Tactical Advisor Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurfaceVariant),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AviationCyan.copy(alpha = 0.5f)))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Gemini AI",
                    tint = AviationGold,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "GEMINI AI TACTICAL ADVISOR",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationGold
                    )
                    Text(
                        text = "Based on $selectedTeamMember settings (${String.format("%.2f", targetCashoutThreshold)}x target, $riskTolerance mode): AI recommends prioritizing low-spike rebound sequences for max win rate.",
                        fontSize = 11.sp,
                        color = AviationTextSecondary
                    )
                }
            }
        }

        // Squad Roster List
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SQUAD ROSTER & TELEMETRY",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    squadMembers.forEach { (name, role) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AviationSurfaceVariant)
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AviationBorder),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = name,
                                    tint = AviationCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AviationTextPrimary)
                                Text(text = role, fontSize = 11.sp, color = AviationEmerald)
                            }
                        }
                    }
                }
            }
        }
    }
}
