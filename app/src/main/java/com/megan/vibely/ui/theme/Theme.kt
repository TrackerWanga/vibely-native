package com.megan.vibely.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7C3AED),
    secondary = Color(0xFFA78BFA),
    tertiary = Color(0xFF06B6D4),
    background = Color(0xFF06060E),
    surface = Color(0xFF0A0A18),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFF94A3B8),
)

@Composable
fun VibelyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}
