package com.group2.pinam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colors = lightColorScheme(
    primary = Violet,
    onPrimary = Color.White,
    primaryContainer = VioletLight,
    onPrimaryContainer = Violet,
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    outline = Outline,
    error = DeleteRed
)

@Composable
fun PinamTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colors, typography = Typography, content = content)
}
