package com.example.pinam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PinamPurpleColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryPurpleContainer,
    onPrimaryContainer = OnPrimaryPurpleContainer,
    secondary = PrimaryPurple,
    onSecondary = OnPrimaryWhite,
    background = BackgroundWhite,
    onBackground = TextNearBlack,
    surface = SurfaceTintLavender,
    onSurface = TextNearBlack,
    outline = BorderLightGray,
    error = DestructiveRed
)

@Composable
fun PinamTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PinamPurpleColorScheme,
        typography = Typography,
        shapes = PinamShapes,
        content = content
    )
}
