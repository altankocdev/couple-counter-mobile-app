package com.altankoc.couplecounter.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.couplecounter.domain.model.ThemeId
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import com.altankoc.couplecounter.domain.repository.UserPreferencesRepository
import com.altankoc.couplecounter.domain.usecase.SetRelationshipStartDateUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

class SettingsViewModel(
    relationshipRepository: RelationshipRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val setRelationshipStartDate: SetRelationshipStartDateUseCase,
    private val clock: Clock,
    private val timeZone: TimeZone
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        relationshipRepository.relationshipStartDate,
        userPreferencesRepository.themeId
    ) { startDate, themeId ->
        SettingsUiState(
            today = clock.todayIn(timeZone),
            relationshipStartDate = startDate,
            selectedThemeId = themeId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = SettingsUiState(today = clock.todayIn(timeZone))
    )

    fun onThemeSelected(themeId: ThemeId) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeId(themeId)
        }
    }

    fun onRelationshipStartDateSelected(date: LocalDate) {
        viewModelScope.launch {
            setRelationshipStartDate(date)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}