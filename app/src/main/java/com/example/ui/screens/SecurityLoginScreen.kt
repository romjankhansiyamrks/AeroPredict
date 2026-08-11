package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

enum class AuthMode {
    SIGN_IN, CREATE_ACCOUNT
}

@Composable
fun SecurityLoginScreen(
    officerCallsign: String,
    clearanceLevel: String,
    onLoginWithGoogle: (customEmail: String?) -> Unit,
    onLoginWithEmail: (email: String, pass: String) -> Boolean,
    onCreateAccount: (name: String, email: String, pass: String) -> Boolean,
    onSendPasswordReset: (email: String) -> Boolean,
    onVerifyPin: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    var authMode by remember { mutableStateOf(AuthMode.SIGN_IN) }

    // Input States
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var fullNameInput by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }
    var showGoogleAccountPicker by remember { mutableStateOf(false) }
    var showAddCustomGmailDialog by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

    val deviceAccounts = remember(showGoogleAccountPicker) {
        val accountList = mutableListOf<String>()
        try {
            val accountManager = android.accounts.AccountManager.get(context)
            val accounts = accountManager.getAccountsByType("com.google")
            for (acc in accounts) {
                if (acc.name.contains("@") && !accountList.contains(acc.name)) {
                    accountList.add(acc.name)
                }
            }
        } catch (e: Exception) {
            // Ignore if account manager unavailable
        }

        val defaultAccounts = listOf(
            "romjamrash@gmail.com",
            "romjankhansiyamrksl@gmail.com",
            "aeropredict.user@gmail.com"
        )
        for (acc in defaultAccounts) {
            if (!accountList.contains(acc)) {
                accountList.add(acc)
            }
        }
        accountList
    }

    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "Aura")
    val shieldScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "ShieldScale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AviationBackground)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(12.dp))

            // Tactical Logo Badge Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AviationSurfaceVariant)
                    .border(1.dp, AviationPurpleBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Shield",
                    tint = AviationCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AEROPREDICT SECURITY PORTAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationCyan,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App Brand Icon
            val purplePrimaryColor = AviationPurplePrimary
            Box(
                modifier = Modifier.size(72.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(72.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                purplePrimaryColor.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .scale(shieldScale)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(AviationPurpleContainer, AviationSurface)
                            )
                        )
                        .border(1.5.dp, AviationCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = "Logo",
                        tint = AviationCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (authMode == AuthMode.SIGN_IN) "Sign In to AeroPredict" else "Create AeroPredict Account",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AviationTextPrimary,
                letterSpacing = 0.5.sp
            )

            Text(
                text = if (authMode == AuthMode.SIGN_IN)
                    "Access live predictions, tactical signals, & analytics"
                else
                    "Register your officer credentials for signal clearance",
                fontSize = 12.sp,
                color = AviationTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Mode Selector Tabs (Sign In vs Create Account)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AviationSurface)
                    .border(1.dp, AviationBorder, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (authMode == AuthMode.SIGN_IN) AviationPurpleContainer else Color.Transparent)
                        .clickable { authMode = AuthMode.SIGN_IN }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SIGN IN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (authMode == AuthMode.SIGN_IN) AviationCyan else AviationTextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (authMode == AuthMode.CREATE_ACCOUNT) AviationPurpleContainer else Color.Transparent)
                        .clickable { authMode = AuthMode.CREATE_ACCOUNT }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CREATE ACCOUNT",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (authMode == AuthMode.CREATE_ACCOUNT) AviationCyan else AviationTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Prominent "Sign in with Google" Button
            Button(
                onClick = { showGoogleAccountPicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_google_signin"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Google Multi-Color Styled Badge
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4285F4)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Sign in with Google",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F1F1F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Divider "OR SIGN IN WITH EMAIL"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = AviationBorder,
                    thickness = 1.dp
                )
                Text(
                    text = if (authMode == AuthMode.SIGN_IN) "  OR SIGN IN WITH EMAIL  " else "  OR REGISTER WITH EMAIL  ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationTextMuted,
                    letterSpacing = 1.sp
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = AviationBorder,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Inputs
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (authMode == AuthMode.CREATE_ACCOUNT) {
                    OutlinedTextField(
                        value = fullNameInput,
                        onValueChange = { fullNameInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_full_name"),
                        label = { Text("Full Name", color = AviationTextMuted) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name",
                                tint = AviationCyan
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = AviationSurface,
                            unfocusedContainerColor = AviationSurface,
                            focusedBorderColor = AviationCyan,
                            unfocusedBorderColor = AviationBorder,
                            focusedTextColor = AviationTextPrimary,
                            unfocusedTextColor = AviationTextPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
                }

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_email"),
                    label = { Text("E-mail Address", color = AviationTextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = AviationCyan
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AviationSurface,
                        unfocusedContainerColor = AviationSurface,
                        focusedBorderColor = AviationCyan,
                        unfocusedBorderColor = AviationBorder,
                        focusedTextColor = AviationTextPrimary,
                        unfocusedTextColor = AviationTextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_password"),
                    label = { Text("Password", color = AviationTextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = AviationCyan
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Password",
                                tint = AviationTextMuted
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = AviationSurface,
                        unfocusedContainerColor = AviationSurface,
                        focusedBorderColor = AviationCyan,
                        unfocusedBorderColor = AviationBorder,
                        focusedTextColor = AviationTextPrimary,
                        unfocusedTextColor = AviationTextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                if (authMode == AuthMode.SIGN_IN) {
                    // Forgot Password Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot Password?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationCyan,
                            modifier = Modifier
                                .clickable { showForgotPasswordDialog = true }
                                .padding(4.dp)
                                .testTag("btn_forgot_password")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Primary Action Button
            Button(
                onClick = {
                    if (authMode == AuthMode.SIGN_IN) {
                        onLoginWithEmail(emailInput, passwordInput)
                    } else {
                        onCreateAccount(fullNameInput, emailInput, passwordInput)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_submit_auth"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AviationPurpleContainer)
            ) {
                Icon(
                    imageVector = if (authMode == AuthMode.SIGN_IN) Icons.Default.Lock else Icons.Default.PersonAdd,
                    contentDescription = "Auth Action",
                    tint = AviationCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (authMode == AuthMode.SIGN_IN) "SIGN IN TO SYSTEM" else "CREATE ACCOUNT & SIGN IN",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AviationCyan,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Footer: Quick PIN Bypass Option
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            TextButton(
                onClick = { showPinDialog = true },
                modifier = Modifier.testTag("btn_open_pin_bypass")
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "PIN",
                    tint = AviationTextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Use Master Command PIN",
                    fontSize = 12.sp,
                    color = AviationTextMuted
                )
            }

            Text(
                text = "CLASSIFIED COMMAND CONSOLE • UNAUTHORIZED ACCESS PROHIBITED",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = AviationTextMuted,
                textAlign = TextAlign.Center
            )
        }
    }

    // Forgot Password Reset Dialog
    if (showForgotPasswordDialog) {
        var resetEmailInput by remember { mutableStateOf(emailInput) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Password", color = AviationTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter your Gmail or registered email address to receive password reset instructions.",
                        fontSize = 12.sp,
                        color = AviationTextSecondary
                    )
                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it },
                        label = { Text("Email Address", color = AviationTextMuted) },
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
                        onSendPasswordReset(resetEmailInput)
                        showForgotPasswordDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AviationPurpleContainer)
                ) {
                    Text("SEND RESET LINK", color = AviationCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("CANCEL", color = AviationTextMuted)
                }
            },
            containerColor = AviationSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // PIN Verification Dialog
    if (showPinDialog) {
        var pinInput by remember { mutableStateOf("") }
        var pinError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showPinDialog = false },
            title = { Text("Master PIN Security Access", color = AviationTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter 4-digit master security key",
                        fontSize = 12.sp,
                        color = AviationTextSecondary
                    )
                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it
                            pinError = false
                        },
                        label = { Text("PIN Code", color = AviationTextMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AviationTextPrimary,
                            unfocusedTextColor = AviationTextPrimary,
                            focusedBorderColor = AviationCyan
                        )
                    )
                    if (pinError) {
                        Text("Invalid PIN Code!", color = AviationCrimson, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val verified = onVerifyPin(pinInput)
                        if (verified) {
                            showPinDialog = false
                        } else {
                            pinError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AviationEmerald)
                ) {
                    Text("UNLOCK", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPinDialog = false }) {
                    Text("CANCEL", color = AviationTextMuted)
                }
            },
            containerColor = AviationSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Google Account Picker Dialog (Displays all Gmail accounts on user's phone)
    if (showGoogleAccountPicker) {
        AlertDialog(
            onDismissRequest = { showGoogleAccountPicker = false },
            title = {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4285F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Column {
                            Text(
                                text = "Choose an account",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AviationTextPrimary
                            )
                            Text(
                                text = "to continue to AeroPredict",
                                fontSize = 12.sp,
                                color = AviationTextSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = AviationBorder, thickness = 1.dp)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Gmail accounts on this device:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AviationCyan,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    deviceAccounts.forEach { gmailAddress ->
                        val name = gmailAddress.substringBefore("@")
                            .replace(".", " ")
                            .split(" ")
                            .joinToString(" ") { word ->
                                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            }
                        val initial = name.firstOrNull()?.uppercase() ?: "G"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AviationSurfaceVariant)
                                .border(1.dp, AviationBorder, RoundedCornerShape(12.dp))
                                .clickable {
                                    showGoogleAccountPicker = false
                                    onLoginWithGoogle(gmailAddress)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (gmailAddress.hashCode() % 3) {
                                            0 -> Color(0xFF4285F4)
                                            1 -> Color(0xFF34A853)
                                            else -> Color(0xFFEA4335)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initial,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AviationTextPrimary
                                )
                                Text(
                                    text = gmailAddress,
                                    fontSize = 12.sp,
                                    color = AviationTextSecondary
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "Select Account",
                                tint = AviationCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Transparent)
                            .border(1.dp, AviationPurpleBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                showGoogleAccountPicker = false
                                showAddCustomGmailDialog = true
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Add Account",
                            tint = AviationCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "+ Add or use another Gmail account",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AviationCyan
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGoogleAccountPicker = false }) {
                    Text("CANCEL", color = AviationTextMuted, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = AviationSurface,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Add Custom Gmail Account Dialog
    if (showAddCustomGmailDialog) {
        var customGmailInput by remember { mutableStateOf("") }
        var inputError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddCustomGmailDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Gmail",
                        tint = AviationCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter Gmail Account",
                        color = AviationTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Type any Gmail account address to sign in directly:",
                        fontSize = 12.sp,
                        color = AviationTextSecondary
                    )
                    OutlinedTextField(
                        value = customGmailInput,
                        onValueChange = {
                            customGmailInput = it
                            inputError = false
                        },
                        label = { Text("Gmail Address (e.g., user@gmail.com)", color = AviationTextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AviationTextPrimary,
                            unfocusedTextColor = AviationTextPrimary,
                            focusedBorderColor = AviationCyan
                        )
                    )
                    if (inputError) {
                        Text("Please enter a valid email address!", color = AviationCrimson, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = customGmailInput.trim()
                        if (trimmed.contains("@")) {
                            showAddCustomGmailDialog = false
                            onLoginWithGoogle(trimmed)
                        } else {
                            inputError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AviationPurpleContainer)
                ) {
                    Text("SIGN IN WITH GMAIL", color = AviationCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomGmailDialog = false }) {
                    Text("CANCEL", color = AviationTextMuted)
                }
            },
            containerColor = AviationSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
