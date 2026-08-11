package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val ImmersiveDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF21005D),
    primaryContainer = Color(0xFF381E72),
    onPrimaryContainer = Color(0xFFD0BCFF),
    secondary = Color(0xFF81C784),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFD54F),
    onTertiary = Color.Black,
    background = Color(0xFF0F0F0F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2B2930),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF49454F),
    error = Color(0xFFFFB4AB),
    onError = Color.Black
)

private val ImmersiveLightColorScheme = lightColorScheme(
    primary = Color(0xFF673AB7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7F6),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    tertiary = Color(0xFFF57F17),
    onTertiary = Color.White,
    background = Color(0xFFF4F6F9),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFC62828),
    onError = Color.White
)

@Composable
fun AviatorPredictorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val aviationColors = if (darkTheme) DarkAviationColors else LightAviationColors
    val colorScheme = if (darkTheme) ImmersiveDarkColorScheme else ImmersiveLightColorScheme

    CompositionLocalProvider(
        LocalAviationColors provides aviationColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
