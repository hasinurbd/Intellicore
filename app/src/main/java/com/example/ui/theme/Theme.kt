package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ArtemisDarkColorScheme = darkColorScheme(
    primary = ArtemisOrange,
    onPrimary = Color.White,
    primaryContainer = LunarSurfaceElevated,
    onPrimaryContainer = TextPrimary,
    secondary = ScientificCyan,
    onSecondary = Color.Black,
    secondaryContainer = LunarBorder,
    onSecondaryContainer = TextPrimary,
    tertiary = VolatileIceAqua,
    onTertiary = Color.Black,
    background = LunarDeepNavy,
    onBackground = TextPrimary,
    surface = LunarSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = LunarSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = LunarBorder,
    error = HazardCrimson,
    onError = Color.White
)

@Composable
fun ArtemisNavigatorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArtemisDarkColorScheme,
        typography = Typography,
        content = content
    )
}
