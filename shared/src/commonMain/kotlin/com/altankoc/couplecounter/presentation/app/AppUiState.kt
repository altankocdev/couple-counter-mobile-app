package com.altankoc.couplecounter.presentation.app

import com.altankoc.couplecounter.domain.model.ThemeId

sealed interface AppUiState {
    data object Loading : AppUiState

    data class Ready(
        val hasRelationshipStartDate: Boolean,
        val themeId: ThemeId?
    ) : AppUiState
}