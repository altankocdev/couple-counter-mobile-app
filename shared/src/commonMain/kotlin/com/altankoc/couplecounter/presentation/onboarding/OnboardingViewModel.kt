package com.altankoc.couplecounter.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.couplecounter.domain.model.RelationshipRules
import com.altankoc.couplecounter.domain.model.SaveDateResult
import com.altankoc.couplecounter.domain.usecase.SetRelationshipStartDateUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class OnboardingViewModel(
    private val setRelationshipStartDate: SetRelationshipStartDateUseCase,
    clock: Clock,
    timeZone: TimeZone
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OnboardingUiState(
            maxSelectableDate = RelationshipRules.latestStartDate(clock.todayIn(timeZone))
        )
    )
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _events = Channel<OnboardingEvent>(Channel.BUFFERED)
    val events: Flow<OnboardingEvent> = _events.receiveAsFlow()

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, error = null) }
    }

    fun onContinue() {
        val state = _uiState.value
        val date = state.selectedDate ?: return
        if (state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            when (setRelationshipStartDate(date)) {
                SaveDateResult.Success -> _events.send(OnboardingEvent.Completed)
                SaveDateResult.FutureDateNotAllowed,
                SaveDateResult.PastDateNotAllowed -> _uiState.update {
                    it.copy(isSaving = false, error = OnboardingError.InvalidDate)
                }
            }
        }
    }
}