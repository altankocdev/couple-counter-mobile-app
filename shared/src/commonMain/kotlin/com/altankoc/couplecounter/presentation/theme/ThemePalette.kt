package com.altankoc.couplecounter.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ThemePalette(
    val gradientStops: List<Pair<Float, Color>>,
    val contentTop: Color,
    val contentBottom: Color,
    val contentSecondary: Color,
    val accent: Color,
    val isTopLight: Boolean,
    val isBottomLight: Boolean
)