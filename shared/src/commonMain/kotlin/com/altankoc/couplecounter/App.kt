package com.altankoc.couplecounter

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.altankoc.couplecounter.presentation.app.AppUiState
import com.altankoc.couplecounter.presentation.app.AppViewModel
import com.altankoc.couplecounter.presentation.components.GradientBackground
import com.altankoc.couplecounter.presentation.navigation.AppNavHost
import com.altankoc.couplecounter.presentation.navigation.HomeRoute
import com.altankoc.couplecounter.presentation.navigation.OnboardingRoute
import com.altankoc.couplecounter.presentation.theme.CoupleCounterTheme
import com.altankoc.couplecounter.presentation.theme.ThemePalettes
import org.koin.compose.viewmodel.koinViewModel

private const val STARTUP_FADE_MILLIS = 350
private val LaunchBackground = Color(0xFF0B0B10)

@Composable
fun App() {
    val viewModel = koinViewModel<AppViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        AppUiState.Loading -> LaunchPlaceholder()
        is AppUiState.Ready -> ReadyContent(state)
    }
}

@Composable
private fun ReadyContent(state: AppUiState.Ready) {
    val palette = if (state.hasRelationshipStartDate) {
        ThemePalettes.byId(state.themeId)
    } else {
        ThemePalettes.Onboarding
    }
    val startDestination: Any = remember {
        if (state.hasRelationshipStartDate) HomeRoute else OnboardingRoute
    }

    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, tween(STARTUP_FADE_MILLIS, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LaunchBackground)
    ) {
        CoupleCounterTheme(palette = palette) {
            GradientBackground(
                modifier = Modifier.graphicsLayer { alpha = contentAlpha.value }
            ) {
                AppNavHost(startDestination = startDestination)
            }
        }
    }
}

@Composable
private fun LaunchPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LaunchBackground)
    )
}