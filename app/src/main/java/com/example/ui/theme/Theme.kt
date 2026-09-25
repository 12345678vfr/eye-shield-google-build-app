package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = Color(0xFF281300),
    primaryContainer = AmberContainer,
    onPrimaryContainer = OnAmberContainer,
    secondary = SoftGold,
    onSecondary = Color(0xFF281C00),
    secondaryContainer = Color(0xFF3F300F),
    onSecondaryContainer = Color(0xFFFFE099),
    tertiary = CalmingCyan,
    onTertiary = Color(0xFF00363A),
    background = NightBackground,
    onBackground = Color(0xFFE2E8F0),
    surface = NightSurface,
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = NightSurfaceVariant,
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceContainer = NightSurfaceContainer,
    outline = NightOutline
)

private val LightColorScheme = lightColorScheme(
    primary = AmberDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDDB5),
    onPrimaryContainer = Color(0xFF2C1500),
    secondary = Color(0xFF765B00),
    onSecondary = Color.White,
    tertiary = Color(0xFF006875),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    surfaceContainer = Color(0xFFF1F5F9),
    outline = Color(0xFFCBD5E1)
)

@Composable
fun EyeShieldTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

