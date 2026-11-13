package com.example.pinam.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PinamGhanaColorScheme = lightColorScheme(
    primary = GhanaGoldPrimary,
    onPrimary = GhanaGoldOnPrimary,
    primaryContainer = GhanaGoldContainer,
    onPrimaryContainer = GhanaGoldOnContainer,
    secondary = GhanaGreenSecondary,
    onSecondary = GhanaGreenOnSecondary,
    secondaryContainer = GhanaGreenContainer,
    background = WarmOffWhiteBackground,
    onBackground = NearBlackText,
    surface = SurfaceWhite,
    onSurface = NearBlackText,
    outline = OutlineVariant,
    error = GhanaRedError,
    errorContainer = GhanaRedErrorContainer
)

@Composable
fun PinamTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PinamGhanaColorScheme,
        typography = Typography,
        shapes = PinamShapes,
        content = content
    )
}
