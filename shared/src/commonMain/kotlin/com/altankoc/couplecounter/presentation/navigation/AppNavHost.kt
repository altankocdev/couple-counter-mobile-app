package com.altankoc.couplecounter.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.altankoc.couplecounter.presentation.home.HomeScreen
import com.altankoc.couplecounter.presentation.onboarding.OnboardingRoot
import com.altankoc.couplecounter.presentation.settings.SettingsRoot

private const val PARALLAX_DIVISOR = 4

@Composable
fun AppNavHost(
    startDestination: Any,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable<OnboardingRoute> {
            OnboardingRoot(
                onCompleted = {
                    navController.navigate(HomeRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<HomeRoute>(
            exitTransition = {
                if (targetState.destination.hasRoute<SettingsRoute>()) {
                    slideOutHorizontally { width -> -width / PARALLAX_DIVISOR } + fadeOut()
                } else {
                    fadeOut()
                }
            },
            popEnterTransition = {
                slideInHorizontally { width -> -width / PARALLAX_DIVISOR } + fadeIn()
            }
        ) {
            HomeScreen(onOpenSettings = { navController.navigate(SettingsRoute) })
        }

        composable<SettingsRoute>(
            enterTransition = { slideInHorizontally { width -> width } + fadeIn() },
            popExitTransition = { slideOutHorizontally { width -> width } + fadeOut() }
        ) {
            SettingsRoot(onBack = { navController.popBackStack() })
        }
    }
}