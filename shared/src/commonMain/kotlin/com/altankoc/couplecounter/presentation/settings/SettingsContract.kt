package com.altankoc.couplecounter.presentation.settings

import com.altankoc.couplecounter.domain.model.NotificationPreferences
import com.altankoc.couplecounter.domain.model.RelationshipRules
import com.altankoc.couplecounter.domain.model.ThemeId
import kotlinx.datetime.LocalDate

data class SettingsUiState(
    val today: LocalDate,
    val relationshipStartDate: LocalDate? = null,
    val selectedThemeId: ThemeId? = null,
    val isNotificationPermissionGranted: Boolean = false,
    val notificationPreferences: NotificationPreferences = NotificationPreferences()
) {
    val maxRelationshipStartDate: LocalDate
        get() = RelationshipRules.latestStartDate(today)
}

sealed interface SettingsEvent {
    data object NotificationPermissionDenied : SettingsEvent
}