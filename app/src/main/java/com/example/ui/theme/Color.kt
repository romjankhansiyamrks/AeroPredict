package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AviationColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val purplePrimary: Color,
    val purpleContainer: Color,
    val purpleBorder: Color,
    val cyan: Color,
    val emerald: Color,
    val gold: Color,
    val crimson: Color,
    val amber: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val isDark: Boolean
)

val DarkAviationColors = AviationColors(
    background = Color(0xFF0F0F0F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF2B2930),
    border = Color(0xFF49454F),
    purplePrimary = Color(0xFFD0BCFF),
    purpleContainer = Color(0xFF381E72),
    purpleBorder = Color(0xFF4F378B),
    cyan = Color(0xFFD0BCFF),
    emerald = Color(0xFF81C784),
    gold = Color(0xFFFFD54F),
    crimson = Color(0xFFFFB4AB),
    amber = Color(0xFFFFB74D),
    textPrimary = Color(0xFFE6E1E5),
    textSecondary = Color(0xFFCAC4D0),
    textMuted = Color(0xFF938F99),
    isDark = true
)

val LightAviationColors = AviationColors(
    background = Color(0xFFF4F6F9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE2E8F0),
    border = Color(0xFFCBD5E1),
    purplePrimary = Color(0xFF673AB7),
    purpleContainer = Color(0xFFEDE7F6),
    purpleBorder = Color(0xFFD1C4E9),
    cyan = Color(0xFF0288D1),
    emerald = Color(0xFF2E7D32),
    gold = Color(0xFFF57F17),
    crimson = Color(0xFFC62828),
    amber = Color(0xFFE65100),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF334155),
    textMuted = Color(0xFF64748B),
    isDark = false
)

val LocalAviationColors = staticCompositionLocalOf { DarkAviationColors }

val AviationBackground: Color
    @Composable get() = LocalAviationColors.current.background

val AviationSurface: Color
    @Composable get() = LocalAviationColors.current.surface

val AviationSurfaceVariant: Color
    @Composable get() = LocalAviationColors.current.surfaceVariant

val AviationBorder: Color
    @Composable get() = LocalAviationColors.current.border

val AviationPurplePrimary: Color
    @Composable get() = LocalAviationColors.current.purplePrimary

val AviationPurpleContainer: Color
    @Composable get() = LocalAviationColors.current.purpleContainer

val AviationPurpleBorder: Color
    @Composable get() = LocalAviationColors.current.purpleBorder

val AviationCyan: Color
    @Composable get() = LocalAviationColors.current.cyan

val AviationEmerald: Color
    @Composable get() = LocalAviationColors.current.emerald

val AviationGold: Color
    @Composable get() = LocalAviationColors.current.gold

val AviationCrimson: Color
    @Composable get() = LocalAviationColors.current.crimson

val AviationAmber: Color
    @Composable get() = LocalAviationColors.current.amber

val AviationTextPrimary: Color
    @Composable get() = LocalAviationColors.current.textPrimary

val AviationTextSecondary: Color
    @Composable get() = LocalAviationColors.current.textSecondary

val AviationTextMuted: Color
    @Composable get() = LocalAviationColors.current.textMuted

