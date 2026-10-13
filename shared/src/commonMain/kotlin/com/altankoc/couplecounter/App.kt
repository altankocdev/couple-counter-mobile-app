package com.altankoc.couplecounter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

@Composable
fun App() {
    val viewModel = koinViewModel<AppViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val palette = when (val state = uiState) {
        AppUiState.Loading -> ThemePalettes.Default
        is AppUiState.Ready -> ThemePalettes.byId(state.themeId)
    }

    CoupleCounterTheme(palette = palette) {
        GradientBackground {
            val state = uiState
            if (state is AppUiState.Ready) {
                val startDestination: Any = remember {
                    if (state.hasRelationshipStartDate) HomeRoute else OnboardingRoute
                }
                AppNavHost(startDestination = startDestination)
            }
        }
    }
}