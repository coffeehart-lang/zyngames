package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameShowColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF004D5A),
    onPrimaryContainer = Color(0xFF70F5FF),
    secondary = ElectricGold,
    onSecondary = Color(0xFF3B2D00),
    secondaryContainer = Color(0xFF5B4500),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = NeonPurple,
    onTertiary = Color(0xFF380062),
    tertiaryContainer = Color(0xFF5E178E),
    onTertiaryContainer = Color(0xFFEDDCFF),
    background = StadiumDarkBg,
    onBackground = TextPrimary,
    surface = StadiumSurface,
    onSurface = TextPrimary,
    surfaceVariant = StadiumSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = StadiumCardBorder,
    error = CrimsonRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = GameShowColorScheme,
        typography = Typography,
        content = content
    )
}

