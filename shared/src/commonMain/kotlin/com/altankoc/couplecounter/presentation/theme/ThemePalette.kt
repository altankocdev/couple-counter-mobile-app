package com.altankoc.couplecounter.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ThemePalette(
    val gradientStops: List<Pair<Float, Color>>,
    val textOnLight: Color,
    val textOnDark: Color,
    val textSecondary: Color,
    val accent: Color
)