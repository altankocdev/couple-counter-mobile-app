package com.altankoc.couplecounter.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import com.altankoc.couplecounter.presentation.theme.THEME_TRANSITION_MILLIS

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val gradientStops = CoupleCounterTheme.palette.gradientStops

    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(
            targetState = gradientStops,
            animationSpec = tween(THEME_TRANSITION_MILLIS),
            label = "gradientBackground"
        ) { stops ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colorStops = stops.toTypedArray()))
            )
        }
        content()
    }
}