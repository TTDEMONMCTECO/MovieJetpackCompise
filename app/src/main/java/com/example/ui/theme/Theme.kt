package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CineVerseColorScheme = darkColorScheme(
    primary = CinemaRed,
    onPrimary = Color.White,
    primaryContainer = CinemaRedDark,
    onPrimaryContainer = Color.White,
    secondary = GoldRating,
    onSecondary = Color.Black,
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = AccentBlue,
    onTertiary = Color.White,
    background = MidnightBackground,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderDark
)

@Composable
fun CineVerseTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CineVerseColorScheme,
        typography = Typography,
        content = content
    )
}

