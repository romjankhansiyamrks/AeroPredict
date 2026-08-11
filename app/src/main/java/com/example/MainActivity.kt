package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.notification.NotificationHelper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.AviatorViewModel
import com.example.ui.screens.AccuracyMetricsScreen
import com.example.ui.screens.CockpitDashboardScreen
import com.example.ui.screens.SecurityLoginScreen
import com.example.ui.screens.SignalHistoryScreen
import com.example.ui.screens.TeamStrategyScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.theme.AviationBorder
import com.example.ui.theme.AviationCyan
import com.example.ui.theme.AviationSurface
import com.example.ui.theme.AviationSurfaceVariant
import com.example.ui.theme.AviationTextMuted
import com.example.ui.AuthenticationViewModel
import com.example.ui.theme.AviationTextPrimary
import com.example.ui.theme.AviatorPredictorTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Cockpit : Screen("cockpit", "Radar", Icons.Default.FlightTakeoff)
    object History : Screen("history", "Signals", Icons.Default.History)
    object Metrics : Screen("metrics", "Accuracy", Icons.Default.Analytics)
    object Team : Screen("team", "Team", Icons.Default.Group)
    object Profile : Screen("profile", "Officer", Icons.Default.Badge)
}

class MainActivity : ComponentActivity() {

    private val viewModel: AviatorViewModel by viewModels()
    private val authViewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            AviatorPredictorTheme(darkTheme = isDarkTheme) {
                AviatorApp(viewModel = viewModel, authViewModel = authViewModel)
            }
        }
    }
}

@Composable
fun AviatorApp(viewModel: AviatorViewModel, authViewModel: AuthenticationViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Request notification permission for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toastMessage.value = "High-Confidence Radar Alerts Enabled!"
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationHelper.hasNotificationPermission(context)) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()

    val isLoggedInLocal by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isLoggedIn = authUiState.isLoggedIn || isLoggedInLocal

    val userEmail = authUiState.userEmail ?: viewModel.userEmail.collectAsStateWithLifecycle().value
    val loginProvider = authUiState.authProvider.ifBlank { viewModel.loginProvider.collectAsStateWithLifecycle().value }
    val officerName by viewModel.officerName.collectAsStateWithLifecycle()
    val officerCallsign by viewModel.officerCallsign.collectAsStateWithLifecycle()
    val squadRole by viewModel.squadRole.collectAsStateWithLifecycle()
    val clearanceLevel by viewModel.clearanceLevel.collectAsStateWithLifecycle()
    val biometricsEnabled by viewModel.biometricsEnabled.collectAsStateWithLifecycle()
    val autoLockOnIdle by viewModel.autoLockOnIdle.collectAsStateWithLifecycle()
    val securityLogs by viewModel.securityLogs.collectAsStateWithLifecycle()

    val signals by viewModel.filteredSignals.collectAsStateWithLifecycle()
    val allSignalsList by viewModel.allSignals.collectAsStateWithLifecycle()
    val recentRounds by viewModel.recentRounds.collectAsStateWithLifecycle()
    val metrics by viewModel.accuracyMetrics.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedTeamMember by viewModel.selectedTeamMember.collectAsStateWithLifecycle()
    val isScanningAi by viewModel.isScanningAi.collectAsStateWithLifecycle()
    val toastMsg by viewModel.toastMessage.collectAsStateWithLifecycle()

    val pushNotificationsEnabled by viewModel.pushNotificationsEnabled.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val confidenceThreshold by viewModel.confidenceThreshold.collectAsStateWithLifecycle()
    val backgroundScanEnabled by viewModel.backgroundScanEnabled.collectAsStateWithLifecycle()

    val screens = listOf(
        Screen.Cockpit,
        Screen.History,
        Screen.Metrics,
        Screen.Team,
        Screen.Profile
    )

    LaunchedEffect(toastMsg) {
        toastMsg?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    LaunchedEffect(authUiState.errorMessage, authUiState.successMessage) {
        authUiState.errorMessage?.let { err ->
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
            authViewModel.clearMessages()
        }
        authUiState.successMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            authViewModel.clearMessages()
        }
    }

    if (!isLoggedIn) {
        SecurityLoginScreen(
            officerCallsign = officerCallsign,
            clearanceLevel = clearanceLevel,
            onLoginWithGoogle = { customEmail ->
                if (!customEmail.isNullOrBlank()) {
                    authViewModel.selectGoogleAccount(customEmail)
                } else {
                    authViewModel.signInWithGoogle(context)
                }
            },
            onLoginWithEmail = { email, pass ->
                authViewModel.signInWithEmail(email, pass)
                true
            },
            onCreateAccount = { name, email, pass ->
                authViewModel.signUpWithEmail(name, email, pass)
                true
            },
            onSendPasswordReset = { email ->
                authViewModel.sendPasswordReset(email)
                true
            },
            onVerifyPin = { pin -> viewModel.verifyPin(pin) }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = AviationSurface,
                    contentColor = AviationTextPrimary,
                    tonalElevation = 8.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    screens.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AviationCyan,
                                selectedTextColor = AviationCyan,
                                indicatorColor = AviationSurfaceVariant,
                                unselectedIconColor = AviationTextMuted,
                                unselectedTextColor = AviationTextMuted
                            ),
                            modifier = Modifier.testTag("nav_${screen.route}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Cockpit.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Cockpit.route) {
                    CockpitDashboardScreen(
                        signals = allSignalsList,
                        recentRounds = recentRounds,
                        metrics = metrics,
                        selectedTeamMember = selectedTeamMember,
                        isScanningAi = isScanningAi,
                        onPredictSignal = { useGemini ->
                            viewModel.generateSignal(useGemini)
                        },
                        onAddRoundMultiplier = { mult ->
                            viewModel.addRoundMultiplier(mult)
                        },
                        onOpenTeamSelector = {
                            navController.navigate(Screen.Profile.route)
                        }
                    )
                }

                composable(Screen.History.route) {
                    SignalHistoryScreen(
                        signals = signals,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter ->
                            viewModel.selectedFilter.value = filter
                        },
                        onRecordOutcome = { id, actual ->
                            viewModel.recordSignalOutcome(id, actual)
                        },
                        onDeleteSignal = { id ->
                            viewModel.deleteSignal(id)
                        },
                        onClearAllHistory = {
                            viewModel.clearAllHistory()
                        }
                    )
                }

                composable(Screen.Metrics.route) {
                    AccuracyMetricsScreen(
                        metrics = metrics,
                        recentRounds = recentRounds,
                        signals = allSignalsList
                    )
                }

                composable(Screen.Team.route) {
                    TeamStrategyScreen(
                        selectedTeamMember = selectedTeamMember,
                        onSelectTeamMember = { name ->
                            viewModel.selectedTeamMember.value = name
                        }
                    )
                }

                composable(Screen.Profile.route) {
                    val creationDate = authUiState.accountCreationDate.ifBlank { viewModel.accountCreationDate.collectAsStateWithLifecycle().value }
                    UserProfileScreen(
                        userEmail = userEmail,
                        accountCreationDate = creationDate,
                        loginProvider = loginProvider,
                        officerName = officerName,
                        officerCallsign = officerCallsign,
                        squadRole = squadRole,
                        clearanceLevel = clearanceLevel,
                        selectedTeamMember = selectedTeamMember,
                        biometricsEnabled = biometricsEnabled,
                        autoLockEnabled = autoLockOnIdle,
                        isDarkTheme = isDarkTheme,
                        pushNotificationsEnabled = pushNotificationsEnabled,
                        confidenceThreshold = confidenceThreshold,
                        backgroundScanEnabled = backgroundScanEnabled,
                        securityLogs = securityLogs,
                        accuracyMetrics = metrics,
                        onUpdateProfile = { name, callsign, role ->
                            viewModel.updateOfficerProfile(name, callsign, role)
                        },
                        onUpdatePin = { oldPin, newPin ->
                            viewModel.updatePin(oldPin, newPin)
                        },
                        onToggleBiometrics = { enabled ->
                            viewModel.toggleBiometrics(enabled)
                        },
                        onToggleAutoLock = { enabled ->
                            viewModel.toggleAutoLock(enabled)
                        },
                        onToggleDarkTheme = { enabled ->
                            viewModel.toggleDarkTheme(enabled)
                        },
                        onTogglePushNotifications = { enabled ->
                            viewModel.togglePushNotifications(enabled)
                        },
                        onSetConfidenceThreshold = { threshold ->
                            viewModel.setConfidenceThreshold(threshold)
                        },
                        onToggleBackgroundScan = { enabled ->
                            viewModel.toggleBackgroundScan(enabled)
                        },
                        onTriggerTestNotification = {
                            viewModel.triggerTestHighConfidenceNotification()
                        },
                        onLogout = {
                            authViewModel.signOut()
                            viewModel.logout()
                        }
                    )
                }
            }
        }
    }
}
