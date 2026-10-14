package com.altankoc.couplecounter.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
private const val ONBOARDING_EXIT_MILLIS = 700
private const val HOME_ENTER_MILLIS = 1_000
private const val HOME_ENTER_DELAY_MILLIS = 300

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
        composable<OnboardingRoute>(
            exitTransition = {
                fadeOut(tween(ONBOARDING_EXIT_MILLIS, easing = FastOutSlowInEasing))
            }
        ) {
            OnboardingRoot(
                onCompleted = {
                    navController.navigate(HomeRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<HomeRoute>(
            enterTransition = {
                if (initialState.destination.hasRoute<OnboardingRoute>()) {
                    fadeIn(tween(HOME_ENTER_MILLIS, HOME_ENTER_DELAY_MILLIS, FastOutSlowInEasing))
                } else {
                    fadeIn()
                }
            },
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