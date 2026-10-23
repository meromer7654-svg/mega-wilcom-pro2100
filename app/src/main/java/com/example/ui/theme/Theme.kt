package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WilcomColorScheme = darkColorScheme(
    primary = WilcomGold,
    onPrimary = Color.Black,
    primaryContainer = WilcomGoldDark,
    onPrimaryContainer = Color.White,
    secondary = WilcomCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF006064),
    onSecondaryContainer = WilcomCyanLight,
    tertiary = WilcomGreen,
    onTertiary = Color.Black,
    background = WilcomDarkBg,
    onBackground = TextPrimary,
    surface = WilcomPanelBg,
    onSurface = TextPrimary,
    surfaceVariant = WilcomPanelBgLight,
    onSurfaceVariant = TextSecondary,
    outline = WilcomBorder,
    error = WilcomRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // CAD editor is designed with dark professional aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = WilcomColorScheme,
        typography = Typography,
        content = content
    )
}
