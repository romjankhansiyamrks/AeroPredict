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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwitchAccessShortcut
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.AviationSurface
import com.example.ui.theme.AviationSurfaceVariant
import com.example.ui.theme.AviationTextMuted
import com.example.ui.theme.AviationTextPrimary
import com.example.ui.theme.AviationTextSecondary

@Composable
fun UserProfileScreen(
    userEmail: String = "romjamrash@gmail.com",
    accountCreationDate: String = "August 8, 2026",
    loginProvider: String = "Google Account",
    officerName: String,
    officerCallsign: String,
    squadRole: String,
    clearanceLevel: String,
    selectedTeamMember: String,
    biometricsEnabled: Boolean,
    autoLockEnabled: Boolean,
    isDarkTheme: Boolean = true,
    pushNotificationsEnabled: Boolean = true,
    confidenceThreshold: Int = 80,
    backgroundScanEnabled: Boolean = true,
    securityLogs: List<String>,
    accuracyMetrics: AccuracyMetrics,
    onUpdateProfile: (name: String, callsign: String, role: String) -> Unit,
    onUpdatePin: (oldPin: String, newPin: String) -> Boolean,
    onToggleBiometrics: (Boolean) -> Unit,
    onToggleAutoLock: (Boolean) -> Unit,
    onToggleDarkTheme: (Boolean) -> Unit = {},
    onTogglePushNotifications: (Boolean) -> Unit = {},
    onSetConfidenceThreshold: (Int) -> Unit = {},
    onToggleBackgroundScan: (Boolean) -> Unit = {},
    onTriggerTestNotification: () -> Unit = {},
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePinDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AviationBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "COMMAND SECURITY & IDENTITY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationCyan,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Profile & Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextPrimary
                )
            }

            // Edit Profile Button
            Button(
                onClick = { showEditProfileDialog = true },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AviationSurfaceVariant),
                modifier = Modifier.testTag("btn_open_edit_profile")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = AviationCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("EDIT", color = AviationCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Account Overview & Creation Date Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_details_card"),
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
                    Text(
                        text = "ACCOUNT DETAILS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationTextPrimary,
                        letterSpacing = 1.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AviationEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AUTHENTICATED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationEmerald
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AviationSurfaceVariant)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "User Email",
                        tint = AviationCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Email Address", fontSize = 10.sp, color = AviationTextMuted, fontWeight = FontWeight.Bold)
                        Text(
                            text = userEmail,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AviationTextPrimary,
                            modifier = Modifier.testTag("text_profile_email")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Creation Date Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AviationSurfaceVariant)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Account Creation Date",
                        tint = AviationGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Account Creation Date", fontSize = 10.sp, color = AviationTextMuted, fontWeight = FontWeight.Bold)
                        Text(
                            text = accountCreationDate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AviationTextPrimary,
                            modifier = Modifier.testTag("text_profile_creation_date")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Auth Method Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AviationSurfaceVariant)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Auth Provider",
                        tint = AviationEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Authentication Method", fontSize = 10.sp, color = AviationTextMuted, fontWeight = FontWeight.Bold)
                        Text(
                            text = loginProvider,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AviationTextPrimary,
                            modifier = Modifier.testTag("text_profile_auth_provider")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct Sign Out Button
                Button(
                    onClick = { onLogout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_account_sign_out"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AviationCrimson.copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Sign Out",
                        tint = AviationCrimson,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SIGN OUT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationCrimson,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Holographic Officer Badge / ID Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("officer_profile_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(AviationPurpleBorder, AviationBorder)
                )
            )
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Badge
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(AviationPurpleContainer, AviationSurfaceVariant)
                                )
                            )
                            .border(2.dp, AviationCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = officerCallsign.take(2).uppercase(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationCyan
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = officerCallsign,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationTextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Officer",
                                tint = AviationEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = officerName,
                            fontSize = 13.sp,
                            color = AviationTextSecondary
                        )

                        Text(
                            text = userEmail,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationCyan
                        )

                        Text(
                            text = "$squadRole • $loginProvider",
                            fontSize = 11.sp,
                            color = AviationTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Clearance Level Tag
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AviationSurfaceVariant)
                        .border(1.dp, AviationPurpleBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Clearance",
                                tint = AviationCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CLEARANCE LEVEL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationTextMuted
                            )
                        }

                        Text(
                            text = clearanceLevel.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Officer Telemetry Snapshot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProfileStatTile(
                        label = "SIGNALS",
                        value = "${accuracyMetrics.totalSignals}",
                        accentColor = AviationCyan
                    )
                    ProfileStatTile(
                        label = "ACCURACY",
                        value = "${String.format("%.1f", accuracyMetrics.winRatePercentage)}%",
                        accentColor = AviationEmerald
                    )
                    ProfileStatTile(
                        label = "WIN STREAK",
                        value = "${accuracyMetrics.currentWinStreak}",
                        accentColor = AviationGold
                    )
                }
            }
        }

        // Dashboard Theme & Visual Mode Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("dashboard_theme_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(AviationPurpleBorder, AviationBorder)
                )
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme Palette",
                            tint = AviationCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DASHBOARD THEME & APPEARANCE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationTextPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDarkTheme) AviationPurpleContainer else AviationCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isDarkTheme) "DARK MODE" else "LIGHT MODE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Toggle Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Theme Icon",
                            tint = if (isDarkTheme) AviationCyan else AviationGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isDarkTheme) "Dark Tactical Theme" else "Light Command Theme",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AviationTextPrimary
                            )
                            Text(
                                text = if (isDarkTheme) "High-contrast dark OLED canvas for low-light cockpits" else "Clean bright canvas with high visibility for daylight operations",
                                fontSize = 11.sp,
                                color = AviationTextMuted
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleDarkTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AviationCyan,
                            checkedTrackColor = AviationPurpleContainer,
                            uncheckedThumbColor = AviationGold,
                            uncheckedTrackColor = AviationSurfaceVariant
                        ),
                        modifier = Modifier.testTag("switch_dark_theme")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Segmented Theme Selector Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = isDarkTheme,
                        onClick = { onToggleDarkTheme(true) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dark Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AviationPurpleContainer,
                            selectedLabelColor = AviationCyan,
                            selectedLeadingIconColor = AviationCyan
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chip_theme_dark")
                    )

                    FilterChip(
                        selected = !isDarkTheme,
                        onClick = { onToggleDarkTheme(false) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LightMode,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Light Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AviationSurfaceVariant,
                            selectedLabelColor = AviationCyan,
                            selectedLeadingIconColor = AviationGold
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chip_theme_light")
                    )
                }
            }
        }

        // Security Controls & Settings Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AviationBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SECURITY CREDENTIALS & POLICIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextPrimary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Change PIN Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showChangePinDialog = true }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Change PIN",
                            tint = AviationCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Change Security Key / PIN", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AviationTextPrimary)
                            Text("Update 4+ digit command clearance key", fontSize = 11.sp, color = AviationTextMuted)
                        }
                    }
                    Text("UPDATE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AviationCyan)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Biometrics Switch Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometrics",
                            tint = AviationEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Biometric Authentication", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AviationTextPrimary)
                            Text("Fast fingerprint login on start screen", fontSize = 11.sp, color = AviationTextMuted)
                        }
                    }
                    Switch(
                        checked = biometricsEnabled,
                        onCheckedChange = { onToggleBiometrics(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AviationEmerald,
                            checkedTrackColor = AviationPurpleContainer
                        ),
                        modifier = Modifier.testTag("switch_biometrics")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Auto Lock Switch Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Auto Lock",
                            tint = AviationGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Auto-Lock Security Protocol", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AviationTextPrimary)
                            Text("Lock system automatically when idle", fontSize = 11.sp, color = AviationTextMuted)
                        }
                    }
                    Switch(
                        checked = autoLockEnabled,
                        onCheckedChange = { onToggleAutoLock(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AviationGold,
                            checkedTrackColor = AviationPurpleContainer
                        ),
                        modifier = Modifier.testTag("switch_autolock")
                    )
                }
            }
        }

        // High Confidence AI Push Notification Settings Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("push_notifications_card"),
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
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Push Notifications",
                            tint = AviationCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HIGH-CONFIDENCE PUSH ALERTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationTextPrimary,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (pushNotificationsEnabled) AviationCyan.copy(alpha = 0.15f) else AviationSurfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (pushNotificationsEnabled) "ACTIVE" else "DISABLED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (pushNotificationsEnabled) AviationCyan else AviationTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Master Switch Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("High-Confidence Signal Alerts", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AviationTextPrimary)
                        Text("Push system notification when AI detects high probability signal while app is in background", fontSize = 11.sp, color = AviationTextMuted)
                    }
                    Switch(
                        checked = pushNotificationsEnabled,
                        onCheckedChange = { onTogglePushNotifications(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AviationCyan,
                            checkedTrackColor = AviationPurpleContainer
                        ),
                        modifier = Modifier.testTag("switch_push_notifications")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Threshold Selector Title
                Text(
                    text = "Alert Confidence Threshold (Minimum %)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(75, 80, 85, 90).forEach { score ->
                        val selected = confidenceThreshold == score
                        FilterChip(
                            selected = selected,
                            onClick = { onSetConfidenceThreshold(score) },
                            label = { Text("$score%+", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AviationCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = AviationSurfaceVariant,
                                labelColor = AviationTextPrimary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = AviationBorder,
                                selectedBorderColor = AviationCyan
                            ),
                            modifier = Modifier.testTag("chip_threshold_$score")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Background Scan Switch Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Radar Scanner",
                            tint = AviationEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Background AI Radar Engine", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AviationTextPrimary)
                            Text("Continuous telemetry monitoring in background", fontSize = 10.sp, color = AviationTextMuted)
                        }
                    }
                    Switch(
                        checked = backgroundScanEnabled,
                        onCheckedChange = { onToggleBackgroundScan(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AviationEmerald,
                            checkedTrackColor = AviationPurpleContainer
                        ),
                        modifier = Modifier.testTag("switch_background_scan")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dispatch Test Push Alert Button
                Button(
                    onClick = { onTriggerTestNotification() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_trigger_test_notification"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AviationPurpleContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Test Notification",
                        tint = AviationCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DISPATCH TEST PUSH ALERT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AviationCyan,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Active Security Audit Trail Log
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
                            imageVector = Icons.Default.Badge,
                            contentDescription = "Audit Log",
                            tint = AviationCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SECURITY AUDIT LOG",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationTextPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Encrypted Log",
                        fontSize = 10.sp,
                        color = AviationTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    securityLogs.take(6).forEach { logEntry ->
                        Text(
                            text = logEntry,
                            fontSize = 11.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = AviationTextSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AviationSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Developer Contact & Support Option Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("developer_contact_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AviationSurface),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(AviationPurpleBorder, AviationBorder)
                )
            )
        ) {
            val uriHandler = LocalUriHandler.current

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Developer Option",
                            tint = AviationGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEVELOPER CONTACT & SUPPORT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationGold,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AviationGold.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "OFFICIAL DEV",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Developer Email
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AviationSurfaceVariant)
                        .border(1.dp, AviationPurpleBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            try {
                                uriHandler.openUri("mailto:romjankhansiyamrksl@gmail.com")
                            } catch (e: Exception) { }
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Developer Email",
                        tint = AviationCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Developer Email",
                            fontSize = 10.sp,
                            color = AviationTextMuted
                        )
                        Text(
                            text = "romjankhansiyamrksl@gmail.com",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Social Links: Website, WhatsApp, Telegram
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Visit Website Button
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AviationSurfaceVariant)
                            .border(1.dp, AviationCyan, RoundedCornerShape(10.dp))
                            .clickable {
                                try {
                                    uriHandler.openUri("https://romjankhansiyam.66ghz.com")
                                } catch (e: Exception) { }
                            }
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Visit Website",
                                tint = AviationCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Visit Website",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationCyan
                            )
                        }
                    }

                    // Contact Support (WhatsApp) Button
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AviationSurfaceVariant)
                            .border(1.dp, AviationEmerald, RoundedCornerShape(10.dp))
                            .clickable {
                                try {
                                    uriHandler.openUri("https://wa.me/+8801910743634")
                                } catch (e: Exception) { }
                            }
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Contact Support",
                                tint = AviationEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Contact Support",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationEmerald
                            )
                        }
                    }

                    // Join Telegram Button
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AviationSurfaceVariant)
                            .border(1.dp, AviationCyan, RoundedCornerShape(10.dp))
                            .clickable {
                                try {
                                    uriHandler.openUri("https://t.me/romjankhansiyamrksl_bot")
                                } catch (e: Exception) { }
                            }
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Join Telegram",
                                tint = AviationCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Join Telegram",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationCyan
                            )
                        }
                    }
                }
            }
        }

        // Lock & Terminate Session Button
        Button(
            onClick = { onLogout() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_lock_system_logout"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AviationCrimson.copy(alpha = 0.2f))
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = AviationCrimson,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "LOCK SYSTEM & TERMINATE SESSION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AviationCrimson,
                letterSpacing = 1.sp
            )
        }
    }

    // Dialog for Editing Officer Details
    if (showEditProfileDialog) {
        var inputName by remember { mutableStateOf(officerName) }
        var inputCallsign by remember { mutableStateOf(officerCallsign) }
        var inputRole by remember { mutableStateOf(squadRole) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Officer Profile", color = AviationTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = inputCallsign,
                        onValueChange = { inputCallsign = it },
                        label = { Text("Officer Callsign", color = AviationTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AviationTextPrimary,
                            unfocusedTextColor = AviationTextPrimary,
                            focusedBorderColor = AviationCyan
                        )
                    )
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = { Text("Full Name / Title", color = AviationTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AviationTextPrimary,
                            unfocusedTextColor = AviationTextPrimary,
                            focusedBorderColor = AviationCyan
                        )
                    )
                    OutlinedTextField(
                        value = inputRole,
                        onValueChange = { inputRole = it },
                        label = { Text("Squad Role", color = AviationTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AviationTextPrimary,
                            unfocusedTextColor = AviationTextPrimary,
                            focusedBorderColor = AviationCyan
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateProfile(inputName, inputCallsign, inputRole)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AviationPurpleContainer)
                ) {
                    Text("SAVE PROFILE", color = AviationCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("CANCEL", color = AviationTextMuted)
                }
            },
            containerColor = AviationSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Dialog for Changing Security PIN
    if (showChangePinDialog) {
        var oldPinInput by remember { mutableStateOf("") }
        var newPinInput by remember { mutableStateOf("") }
        var pinErrorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showChangePinDialog = false },
            title = { Text("Update Security PIN", color = AviationTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = oldPinInput,
                        onValueChange = { oldPinInput = it },
                        label = { Text("Current PIN Key", color = AviationTextMuted) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AviationTextPrimary,
                            unfocusedTextColor = AviationTextPrimary,
                            focusedBorderColor = AviationCyan
                        )
                    )
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { newPinInput = it },
                        label = { Text("New PIN Key (4+ digits)", color = AviationTextMuted) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AviationTextPrimary,
                            unfocusedTextColor = AviationTextPrimary,
                            focusedBorderColor = AviationCyan
                        )
                    )

                    pinErrorMessage?.let { err ->
                        Text(err, color = AviationCrimson, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = onUpdatePin(oldPinInput, newPinInput)
                        if (success) {
                            showChangePinDialog = false
                        } else {
                            pinErrorMessage = "Invalid current PIN or new PIN is invalid."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AviationEmerald)
                ) {
                    Text("UPDATE PIN", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangePinDialog = false }) {
                    Text("CANCEL", color = AviationTextMuted)
                }
            },
            containerColor = AviationSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun ProfileStatTile(
    label: String,
    value: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
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
