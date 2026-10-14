package com.altankoc.couplecounter.presentation.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal const val THEME_TRANSITION_MILLIS = 600

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
    val animatedPalette = animatePalette(palette)

    SystemBarsAppearance(
        isTopLight = palette.isTopLight,
        isBottomLight = palette.isBottomLight
    )

    val colorScheme = darkColorScheme(
        primary = animatedPalette.accent,
        onPrimary = animatedPalette.contentBottom,
        background = palette.gradientStops.last().second,
        onBackground = animatedPalette.contentBottom,
        surface = palette.gradientStops.last().second,
        onSurface = animatedPalette.contentBottom
    )

    CompositionLocalProvider(
        LocalThemePalette provides animatedPalette,
        LocalAppFonts provides fonts
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = appTypography(fonts.sans),
            content = content
        )
    }
}

@Composable
private fun animatePalette(target: ThemePalette): ThemePalette {
    val contentTop by animateThemeColor(target.contentTop, "contentTop")
    val contentBottom by animateThemeColor(target.contentBottom, "contentBottom")
    val contentSecondary by animateThemeColor(target.contentSecondary, "contentSecondary")
    val accent by animateThemeColor(target.accent, "accent")

    return target.copy(
        contentTop = contentTop,
        contentBottom = contentBottom,
        contentSecondary = contentSecondary,
        accent = accent
    )
}

@Composable
private fun animateThemeColor(target: Color, label: String) =
    animateColorAsState(
        targetValue = target,
        animationSpec = tween(THEME_TRANSITION_MILLIS),
        label = label
    )

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