package com.altankoc.couplecounter.presentation.theme

import androidx.compose.ui.graphics.Color
import com.altankoc.couplecounter.domain.model.ThemeId

object ThemePalettes {

    private object HolstColors {
        val DeepNavy = Color(0xFF0D1B2A)
        val Navy = Color(0xFF1B263B)
        val SlateBlue = Color(0xFF415A77)
        val Sage = Color(0xFF778D7A)
        val Sand = Color(0xFFD4C4A8)
        val Cream = Color(0xFFF4F1DE)
    }

    val Holst = ThemePalette(
        id = ThemeId("holst"),
        name = "Holst",
        gradientStops = listOf(
            0.00f to HolstColors.Sand,
            0.12f to HolstColors.Sand,
            0.42f to HolstColors.SlateBlue,
            0.65f to HolstColors.Navy,
            1.00f to HolstColors.DeepNavy
        ),
        contentTop = HolstColors.Navy,
        contentBottom = HolstColors.Cream,
        contentSecondary = HolstColors.Sand,
        accent = HolstColors.Sage,
        isTopLight = true,
        isBottomLight = false
    )

    val Default = Holst

    val all: List<ThemePalette> = listOf(Holst)

    fun byId(id: ThemeId?): ThemePalette = all.firstOrNull { it.id == id } ?: Default
}