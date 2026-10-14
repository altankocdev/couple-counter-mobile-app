package com.altankoc.couplecounter.presentation.theme

import androidx.compose.ui.graphics.Color
import com.altankoc.couplecounter.domain.model.ThemeId

object ThemePalettes {

    private object NocturneColors {
        val DeepNavy = Color(0xFF0D1B2A)
        val Navy = Color(0xFF1B263B)
        val SlateBlue = Color(0xFF415A77)
        val Sage = Color(0xFF778D7A)
        val Sand = Color(0xFFD4C4A8)
        val Cream = Color(0xFFF4F1DE)
    }

    val Nocturne = ThemePalette(
        id = ThemeId("theme_01"),
        name = "Nocturne",
        gradientStops = listOf(
            0.00f to NocturneColors.Sand,
            0.12f to NocturneColors.Sand,
            0.42f to NocturneColors.SlateBlue,
            0.65f to NocturneColors.Navy,
            1.00f to NocturneColors.DeepNavy
        ),
        contentTop = NocturneColors.Navy,
        contentBottom = NocturneColors.Cream,
        contentSecondary = NocturneColors.Sand,
        accent = NocturneColors.Sage,
        isTopLight = true,
        isBottomLight = false
    )

    val Default = Nocturne

    val all: List<ThemePalette> = listOf(Nocturne)

    fun byId(id: ThemeId?): ThemePalette = all.firstOrNull { it.id == id } ?: Default
}