package com.altankoc.couplecounter.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import com.altankoc.couplecounter.domain.repository.UserPreferencesRepository
import com.altankoc.couplecounter.domain.usecase.RescheduleRemindersUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
    relationshipRepository: RelationshipRepository,
    userPreferencesRepository: UserPreferencesRepository,
    private val rescheduleReminders: RescheduleRemindersUseCase
) : ViewModel() {

    val uiState: StateFlow<AppUiState> = combine(
        relationshipRepository.relationshipStartDate,
        userPreferencesRepository.themeId
    ) { startDate, themeId ->
        AppUiState.Ready(
            hasRelationshipStartDate = startDate != null,
            themeId = themeId
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = AppUiState.Loading
    )

    init {
        viewModelScope.launch { rescheduleReminders() }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}