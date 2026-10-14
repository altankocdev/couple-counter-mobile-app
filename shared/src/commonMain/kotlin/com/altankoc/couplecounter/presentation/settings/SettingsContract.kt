package com.altankoc.couplecounter.presentation.settings

import com.altankoc.couplecounter.domain.model.ThemeId
import kotlinx.datetime.LocalDate

data class SettingsUiState(
    val today: LocalDate,
    val relationshipStartDate: LocalDate? = null,
    val selectedThemeId: ThemeId? = null
)