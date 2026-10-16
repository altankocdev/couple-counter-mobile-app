package com.altankoc.couplecounter.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.altankoc.couplecounter.domain.model.NotificationPreferences
import com.altankoc.couplecounter.domain.model.SaveDateResult
import com.altankoc.couplecounter.domain.model.ThemeId
import com.altankoc.couplecounter.domain.repository.NotificationPermissionProvider
import com.altankoc.couplecounter.domain.repository.RelationshipRepository
import com.altankoc.couplecounter.domain.repository.UserPreferencesRepository
import com.altankoc.couplecounter.domain.usecase.RescheduleRemindersUseCase
import com.altankoc.couplecounter.domain.usecase.SetRelationshipStartDateUseCase
import com.altankoc.couplecounter.presentation.notification.NotificationPermissionResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val notificationPermissionProvider: NotificationPermissionProvider,
    private val rescheduleReminders: RescheduleRemindersUseCase,
    private val clock: Clock,
    private val timeZone: TimeZone
) : ViewModel() {

    private val isPermissionGranted = MutableStateFlow(false)

    private val _events = Channel<SettingsEvent>(Channel.BUFFERED)
    val events: Flow<SettingsEvent> = _events.receiveAsFlow()

    val uiState: StateFlow<SettingsUiState> = combine(
        relationshipRepository.relationshipStartDate,
        userPreferencesRepository.themeId,
        userPreferencesRepository.notificationPreferences,
        isPermissionGranted
    ) { startDate, themeId, notificationPreferences, permissionGranted ->
        SettingsUiState(
            today = clock.todayIn(timeZone),
            relationshipStartDate = startDate,
            selectedThemeId = themeId,
            isNotificationPermissionGranted = permissionGranted,
            notificationPreferences = notificationPreferences
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = SettingsUiState(today = clock.todayIn(timeZone))
    )

    init {
        refreshNotificationPermission()
    }

    fun refreshNotificationPermission() {
        viewModelScope.launch {
            val wasGranted = isPermissionGranted.value
            val isGranted = notificationPermissionProvider.isGranted()
            isPermissionGranted.value = isGranted
            if (isGranted != wasGranted) rescheduleReminders()
        }
    }

    fun onNotificationPermissionResult(result: NotificationPermissionResult) {
        viewModelScope.launch {
            when (result) {
                NotificationPermissionResult.Granted -> {
                    isPermissionGranted.value = notificationPermissionProvider.isGranted()
                    if (isPermissionGranted.value) updateNotificationsEnabled(true)
                }
                NotificationPermissionResult.Denied -> {
                    isPermissionGranted.value = notificationPermissionProvider.isGranted()
                    _events.send(SettingsEvent.NotificationPermissionDenied)
                }
                NotificationPermissionResult.OpenedSettings -> Unit
            }
        }
    }

    fun onNotificationsEnabledChanged(enabled: Boolean) {
        viewModelScope.launch { updateNotificationsEnabled(enabled) }
    }

    fun onNotificationPreferencesChanged(preferences: NotificationPreferences) {
        viewModelScope.launch {
            userPreferencesRepository.setNotificationPreferences(preferences)
            rescheduleReminders()
        }
    }

    fun onThemeSelected(themeId: ThemeId) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeId(themeId)
        }
    }

    fun onRelationshipStartDateSelected(date: LocalDate) {
        viewModelScope.launch {
            if (setRelationshipStartDate(date) == SaveDateResult.Success) {
                rescheduleReminders()
            }
        }
    }

    private suspend fun updateNotificationsEnabled(enabled: Boolean) {
        val current = userPreferencesRepository.notificationPreferences.first()
        if (current.enabled != enabled) {
            userPreferencesRepository.setNotificationPreferences(current.copy(enabled = enabled))
        }
        rescheduleReminders()
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}