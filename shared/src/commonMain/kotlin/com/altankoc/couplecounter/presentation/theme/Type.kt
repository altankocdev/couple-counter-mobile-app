package com.altankoc.couplecounter.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.altankoc.couplecounter.resources.Res
import com.altankoc.couplecounter.resources.great_vibes_regular
import com.altankoc.couplecounter.resources.playfair_display_regular
import com.altankoc.couplecounter.resources.plus_jakarta_sans_bold
import com.altankoc.couplecounter.resources.plus_jakarta_sans_light
import com.altankoc.couplecounter.resources.plus_jakarta_sans_medium
import com.altankoc.couplecounter.resources.plus_jakarta_sans_regular
import com.altankoc.couplecounter.resources.plus_jakarta_sans_semibold
import org.jetbrains.compose.resources.Font

internal const val TABULAR_NUMBERS = "tnum"

@Immutable
data class AppFonts(
    val sans: FontFamily,
    val serif: FontFamily,
    val script: FontFamily
)

@Composable
internal fun appFonts(): AppFonts = AppFonts(
    sans = FontFamily(
        Font(Res.font.plus_jakarta_sans_light, FontWeight.Light),
        Font(Res.font.plus_jakarta_sans_regular, FontWeight.Normal),
        Font(Res.font.plus_jakarta_sans_medium, FontWeight.Medium),
        Font(Res.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
        Font(Res.font.plus_jakarta_sans_bold, FontWeight.Bold)
    ),
    serif = FontFamily(Font(Res.font.playfair_display_regular, FontWeight.Normal)),
    script = FontFamily(Font(Res.font.great_vibes_regular, FontWeight.Normal))
)

internal fun appTypography(family: FontFamily): Typography {
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.copy(fontFamily = family),
        displayMedium = base.displayMedium.copy(fontFamily = family),
        displaySmall = base.displaySmall.copy(fontFamily = family),
        headlineLarge = base.headlineLarge.copy(fontFamily = family),
        headlineMedium = base.headlineMedium.copy(fontFamily = family),
        headlineSmall = base.headlineSmall.copy(fontFamily = family),
        titleLarge = base.titleLarge.copy(fontFamily = family),
        titleMedium = base.titleMedium.copy(fontFamily = family),
        titleSmall = base.titleSmall.copy(fontFamily = family),
        bodyLarge = base.bodyLarge.copy(fontFamily = family),
        bodyMedium = base.bodyMedium.copy(fontFamily = family),
        bodySmall = base.bodySmall.copy(fontFamily = family),
        labelLarge = base.labelLarge.copy(fontFamily = family),
        labelMedium = base.labelMedium.copy(fontFamily = family),
        labelSmall = base.labelSmall.copy(fontFamily = family)
    )
}