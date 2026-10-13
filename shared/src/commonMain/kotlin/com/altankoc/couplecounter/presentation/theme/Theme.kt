package com.altankoc.couplecounter.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalThemePalette = staticCompositionLocalOf { ThemePalettes.Default }
private val LocalAppFonts = staticCompositionLocalOf<AppFonts> {
    error("AppFonts not provided. Wrap content with CoupleCounterTheme.")
}

@Composable
fun CoupleCounterTheme(
    palette: ThemePalette = ThemePalettes.Default,
    content: @Composable () -> Unit
) {
    val fonts = appFonts()
    val colorScheme = darkColorScheme(
        primary = palette.accent,
        onPrimary = palette.textOnDark,
        background = palette.gradientStops.last().second,
        onBackground = palette.textOnDark,
        surface = palette.gradientStops.last().second,
        onSurface = palette.textOnDark
    )

    CompositionLocalProvider(
        LocalThemePalette provides palette,
        LocalAppFonts provides fonts
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = appTypography(fonts.sans),
            content = content
        )
    }
}

object CoupleCounterTheme {
    val palette: ThemePalette
        @Composable
        @ReadOnlyComposable
        get() = LocalThemePalette.current

    val fonts: AppFonts
        @Composable
        @ReadOnlyComposable
        get() = LocalAppFonts.current
}