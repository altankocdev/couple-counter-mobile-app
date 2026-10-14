package com.altankoc.couplecounter.presentation.theme

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
        onPrimary = animatedPalette.onAccent,
        background = animatedPalette.surface,
        onBackground = animatedPalette.contentBottom,
        surface = animatedPalette.surface,
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
    val contentTop by animateContentColor(target.contentTop)
    val contentBottom by animateContentColor(target.contentBottom)
    val contentSecondary by animateContentColor(target.contentSecondary)
    val accent by animateContentColor(target.accent)
    val onAccent by animateContentColor(target.onAccent)
    val surface by animateColorAsState(
        targetValue = target.surface,
        animationSpec = tween(THEME_TRANSITION_MILLIS),
        label = "surface"
    )

    return target.copy(
        contentTop = contentTop,
        contentBottom = contentBottom,
        contentSecondary = contentSecondary,
        accent = accent,
        onAccent = onAccent,
        surface = surface
    )
}

@Composable
private fun animateContentColor(target: Color): State<Color> {
    val animatable = remember { Animatable(target) }

    LaunchedEffect(target) {
        if (animatable.value == target) return@LaunchedEffect
        val halfDuration = THEME_TRANSITION_MILLIS / 2
        animatable.animateTo(animatable.value.copy(alpha = 0f), tween(halfDuration))
        animatable.snapTo(target.copy(alpha = 0f))
        animatable.animateTo(target, tween(halfDuration))
    }

    return animatable.asState()
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