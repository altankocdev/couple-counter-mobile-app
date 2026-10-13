package com.altankoc.couplecounter.presentation.theme

import androidx.compose.ui.graphics.Color

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
        gradientStops = listOf(
            0.00f to HolstColors.Sand,
            0.12f to HolstColors.Sand,
            0.42f to HolstColors.SlateBlue,
            0.65f to HolstColors.Navy,
            1.00f to HolstColors.DeepNavy
        ),
        textOnLight = HolstColors.Navy,
        textOnDark = HolstColors.Cream,
        textSecondary = HolstColors.Sand,
        accent = HolstColors.Sage
    )

    val Default = Holst
}