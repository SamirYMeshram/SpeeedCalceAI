package com.sameer.speedmath.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object SmColor {
    val Background = Color(0xFF08111F)
    val Surface = Color(0xFF0D192B)
    val Surface2 = Color(0xFF111F35)
    val Surface3 = Color(0xFF17283F)
    val Stroke = Color(0xFF223A58)
    val Primary = Color(0xFF00D9FF)
    val PrimarySoft = Color(0xFF1B8DFF)
    val Purple = Color(0xFF9B5CFF)
    val Pink = Color(0xFFFF4D89)
    val Green = Color(0xFF24E19B)
    val Red = Color(0xFFFF5A6E)
    val Orange = Color(0xFFFFB74A)
    val Yellow = Color(0xFFFFD166)
    val Text = Color(0xFFEAF6FF)
    val TextMuted = Color(0xFF91A4BD)
    val TextSubtle = Color(0xFF60748F)
    val BlueGradient = Brush.linearGradient(listOf(Color(0xFF123C8C), Color(0xFF0F97C8)))
    val ChallengeGradient = Brush.linearGradient(listOf(Color(0xFF402057), Color(0xFFAA1E62)))
    val BattleGradient = Brush.linearGradient(listOf(Color(0xFF3D1D85), Color(0xFFE4477B)))
    val CyanGradient = Brush.linearGradient(listOf(Color(0xFF0A4A67), Color(0xFF0EBDD9)))
}

private val DarkColors = darkColorScheme(
    primary = SmColor.Primary,
    secondary = SmColor.Purple,
    background = SmColor.Background,
    surface = SmColor.Surface,
    onPrimary = Color(0xFF001018),
    onBackground = SmColor.Text,
    onSurface = SmColor.Text,
    error = SmColor.Red
)

@Composable
fun SpeedMathTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = Typography(
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(color = SmColor.Text),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(color = SmColor.Text),
            titleLarge = MaterialTheme.typography.titleLarge.copy(color = SmColor.Text),
            titleMedium = MaterialTheme.typography.titleMedium.copy(color = SmColor.Text),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(color = SmColor.Text),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(color = SmColor.TextMuted),
            labelLarge = MaterialTheme.typography.labelLarge.copy(color = SmColor.Text)
        ),
        content = content
    )
}
