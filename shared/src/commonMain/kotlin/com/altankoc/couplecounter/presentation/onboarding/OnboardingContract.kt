package com.altankoc.couplecounter.presentation.onboarding

import kotlinx.datetime.LocalDate

data class OnboardingUiState(
    val maxSelectableDate: LocalDate,
    val selectedDate: LocalDate? = null,
    val isSaving: Boolean = false,
    val error: OnboardingError? = null
) {
    val canContinue: Boolean
        get() = selectedDate != null && !isSaving
}

enum class OnboardingError {
    InvalidDate
}

sealed interface OnboardingEvent {
    data object Completed : OnboardingEvent
}