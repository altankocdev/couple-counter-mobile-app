package com.altankoc.couplecounter.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.altankoc.couplecounter.domain.model.ThemeId

@Immutable
data class ThemePalette(
    val id: ThemeId,
    val name: String,
    val gradientStops: List<Pair<Float, Color>>,
    val contentTop: Color,
    val contentBottom: Color,
    val contentSecondary: Color,
    val accent: Color,
    val onAccent: Color,
    val isTopLight: Boolean,
    val isBottomLight: Boolean
)